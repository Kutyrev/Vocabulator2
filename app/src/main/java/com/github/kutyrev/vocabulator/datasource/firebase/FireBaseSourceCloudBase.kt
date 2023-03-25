package com.github.kutyrev.vocabulator.datasource.firebase

import android.util.Log
import com.github.kutyrev.vocabulator.BuildConfig
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.translator.TranslationCallback
import com.github.kutyrev.vocabulator.repository.translator.TranslationResultStatus
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

private const val TAG = "FireBaseSourceCloudBase"

private const val FIREBASE_QUERY_LIMIT = 10
private const val INCOME_PATH = "income"
private const val ERROR_CANT_GET_DOC = "Can't get doc ref"

class FireBaseSourceCloudBase : CloudBase {

    override suspend fun getTranslation(
        wordsToTranslate: List<WordCard>,
        origLanguage: Language,
        transLanguage: Language,
        translationCallback: TranslationCallback
    ) {
        if (origLanguage == Language.EN) {

            val db = Firebase.firestore

            val subLists: MutableList<List<WordCard>> = mutableListOf()

            for (i in wordsToTranslate.indices step FIREBASE_QUERY_LIMIT) {
                subLists.add(
                    wordsToTranslate.subList(
                        i,
                        if (i + FIREBASE_QUERY_LIMIT > wordsToTranslate.size - 1) wordsToTranslate.size - 1 else i + FIREBASE_QUERY_LIMIT
                    )
                )
            }

            val tasks: MutableList<Task<QuerySnapshot>> = mutableListOf()

            //  Делим на порции по 10. Обходим ограничение Invalid Query.
            //  'in' filters support a maximum of 10 elements in the value array.
            subLists.forEach { curSubList ->
                tasks.add(
                    db.collection(origLanguage.name.lowercase(locale = Locale.getDefault()))
                        .whereIn(FieldPath.documentId(), curSubList.map { it.originalWord })
                        .get()
                )
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener {
                var allTaskSuccessfull = true
                var exceptionDesc = ""

                it.result.forEach { task ->
                    if (!task.isSuccessful) {
                        allTaskSuccessfull = false
                        exceptionDesc = task.exception.toString()
                    }
                }

                if (!allTaskSuccessfull) {
                    translationCallback.receiveTranslation(
                        wordsToTranslate,
                        TranslationResultStatus.FirebaseError
                    )
                } else {
                    it.result.forEach { task ->
                        if (task.isSuccessful) {
                            val documents = (task as Task<QuerySnapshot>).result.documents
                            for (curDocument in documents) {
                                val curID = curDocument.id
                                for (k in wordsToTranslate.indices) {
                                    val curWord = wordsToTranslate[k]
                                    if (curWord.originalWord == curID) {
                                        curWord.translatedWord =
                                            (curDocument[transLanguage.name.lowercase(locale = Locale.getDefault())] as String?).toString()
                                    }
                                }
                            }
                        }
                    }

                    translationCallback.receiveTranslation(
                        wordsToTranslate,
                        TranslationResultStatus.FirebaseSuccess
                    )
                }
            }
        }
    }

    override suspend fun saveNewWords(
        wordsToSave: List<WordCard>,
        origLanguage: Language,
        transLanguage: Language
    ) {
        if (origLanguage == Language.EN) {
            for (word in wordsToSave) {
                addWordToFirestoreIncome(
                    word.originalWord,
                    word.translatedWord,
                    origLanguage.name,
                    transLanguage.name
                )
            }
        }
    }

    private fun addWordToFirestoreIncome(
        originalWord: String,
        translatedWord: String,
        origLanguage: String,
        transLanguage: String
    ) {
        val db = Firebase.firestore
        val docRef = db.collection(INCOME_PATH).document(originalWord)

        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val data: MutableMap<String, Any> =
                    HashMap()
                val document = task.result

                if (document.exists()) {
                    data[transLanguage.lowercase()] = translatedWord
                    docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            if (BuildConfig.DEBUG) {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot $originalWord merged."
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            if (BuildConfig.DEBUG) {
                                Log.w(
                                    TAG,
                                    "Error writing document $originalWord", e
                                )
                            }
                        }

                } else {
                    //Word not exist in income table
                    data[Language.EN.name.lowercase()] = ""
                    data[Language.FR.name.lowercase()] = ""
                    data[Language.RU.name.lowercase()] = ""
                    data[Language.IT.name.lowercase()] = ""
                    data[origLanguage.lowercase()] = originalWord
                    data[transLanguage.lowercase()] = translatedWord
                    docRef.set(data)
                        .addOnSuccessListener {
                            if (BuildConfig.DEBUG) {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot $originalWord written."
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            if (BuildConfig.DEBUG) {
                                Log.w(
                                    TAG,
                                    "Error writing document $originalWord", e
                                )
                            }
                        }
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, ERROR_CANT_GET_DOC, task.exception)
                }
            }
        }
    }
}
