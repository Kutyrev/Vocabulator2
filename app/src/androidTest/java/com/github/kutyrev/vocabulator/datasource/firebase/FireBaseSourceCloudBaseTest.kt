package com.github.kutyrev.vocabulator.datasource.firebase

import android.util.Log
import com.github.kutyrev.vocabulator.model.Language
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.repository.translator.TranslationCallback
import com.github.kutyrev.vocabulator.repository.translator.TranslationResultStatus
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val expectedString = "наказуемость; сознание вины; вина; проступок; преступление"

internal class FireBaseSourceCloudBaseTest : TranslationCallback {

    private val mockTranslationWords = listOf(WordCard(1, 1, "guilt", ""))
    private val origLang = Language.EN
    private val transLang = Language.RU
    private var resultTranslation = ""
    private var translationResultStatus: TranslationResultStatus? = null
    var latch = CountDownLatch(1)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getTranslationTest() = runTest {
        val firebaseSource = FireBaseSourceCloudBase()
        firebaseSource.getTranslation(
            mockTranslationWords,
            origLang,
            transLang,
            this@FireBaseSourceCloudBaseTest
        )
        latch.await(2000, TimeUnit.MILLISECONDS)
        assertTrue(translationResultStatus is TranslationResultStatus.FirebaseSuccess)
        assertEquals(expectedString, resultTranslation)

    }

    override fun receiveTranslation(
        translatedWords: List<WordCard>,
        translationResult: TranslationResultStatus
    ) {
        translationResultStatus = translationResult
        resultTranslation = translatedWords[0].translatedWord
        Log.d("FireBaseSourceCloudBaseTest", translatedWords[0].translatedWord)
        latch.countDown()
    }
}
