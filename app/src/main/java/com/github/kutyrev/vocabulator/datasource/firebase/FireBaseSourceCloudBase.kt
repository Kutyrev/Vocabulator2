package com.github.kutyrev.vocabulator.datasource.firebase

import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.translator.TranslationCallback
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FireBaseSourceCloudBase : CloudBase {

    override suspend fun getTranslation(
        wordsToTranslate: List<WordCard>,
        origLanguage: Language,
        transLanguage: Language,
        translationCallback: TranslationCallback?
    ) {
        if (origLanguage == Language.EN) {

            val db = Firebase.firestore

            val subLists: MutableList<List<WordCard>> = mutableListOf()

            for (i in wordsToTranslate.indices step 10) {
                subLists.add(
                    wordsToTranslate.subList(
                        i,
                        if (i + 10 > wordsToTranslate.size - 1) wordsToTranslate.size - 1 else i + 10
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

               translationCallback?.receiveTranslation(wordsToTranslate)
            }
        }
    }
}
