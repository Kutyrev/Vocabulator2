package com.github.kutyrev.vocabulator.datasource.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

private const val CORRECT_ARRAY_SIZE = 1
private const val CORRECT_FULL_ARRAY_SIZE = 2
private const val SUBS_ID = 1
private const val SUBS_ID_TWO = 2
private const val LANG_ID = 1
private const val LANG_ID_TWO = 2
private const val MODIFIED_NAME = "mod_mock"

@RunWith(AndroidJUnit4::class)
class DatabaseSourceTest {
    private lateinit var vocabulatorDao: VocabulatorDao
    private lateinit var db: DatabaseSource

    private val subUnitMock = SubtitlesUnit(
        SUBS_ID, "Mock",
        LANG_ID, 2
    )
    private val subUnitMock2 = SubtitlesUnit(
        SUBS_ID_TWO, "Mock2",
        LANG_ID, 2
    )

    private val wordCardsMock = listOf(
        WordCard(
            SUBS_ID,
            SUBS_ID, "mock", "mock"
        )
    )

    private val wordCardMock2 = WordCard(
        SUBS_ID_TWO,
        SUBS_ID_TWO, "mock2", "mock2"
    )

    private val commonWordsMock = listOf(
        CommonWord(
            1,
            LANG_ID, "mock"
        )
    )

    private val commonWordMock = CommonWord(
        2,
        LANG_ID_TWO, "mock2"
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DatabaseSource::class.java
        ).build()
        vocabulatorDao = db.vocabulatorDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun subtitlesUnitTest() = runTest {
        vocabulatorDao.insertSubtitlesInfo(subUnitMock)
        vocabulatorDao.insertSubtitlesInfo(subUnitMock2)

        val receivedSubtitles: List<SubtitlesUnit> = vocabulatorDao.getSubtitlesList().first()
        assertTrue(receivedSubtitles.size == CORRECT_FULL_ARRAY_SIZE)
        assertEquals(subUnitMock, receivedSubtitles[0])

        val receivedSubtitle: SubtitlesUnit = vocabulatorDao.getSubtitleUnit(SUBS_ID)
        assertEquals(subUnitMock, receivedSubtitle)

        subUnitMock.name = MODIFIED_NAME
        vocabulatorDao.updateSubtitlesInfo(subUnitMock)
        val receivedModSubtitle: SubtitlesUnit = vocabulatorDao.getSubtitleUnit(SUBS_ID)
        assertEquals(MODIFIED_NAME, receivedModSubtitle.name)

        vocabulatorDao.deleteSubtitles(subUnitMock2)
        val receivedSubtitlesAfterDeletion: List<SubtitlesUnit> =
            vocabulatorDao.getSubtitlesList().first()
        assertTrue(receivedSubtitlesAfterDeletion.size == CORRECT_ARRAY_SIZE)
        assertEquals(subUnitMock, receivedSubtitlesAfterDeletion[0])
    }

    @Test
    @Throws(Exception::class)
    fun wordCardsTest() = runTest {
        vocabulatorDao.insertSubtitlesInfo(subUnitMock)
        vocabulatorDao.insertWordCards(wordCardsMock)
        vocabulatorDao.insertSubtitlesInfo(subUnitMock2)
        vocabulatorDao.insertWordCard(wordCardMock2)

        val receivedAllWordsCards: List<WordCard> =
            vocabulatorDao.getAllWordsCards().first()
        assertTrue(receivedAllWordsCards.size == CORRECT_FULL_ARRAY_SIZE)
        assertEquals(wordCardsMock[0].originalWord, receivedAllWordsCards[0].originalWord)

        val receivedWordsCards: List<WordCard> =
            vocabulatorDao.getSubtitlesWordsCards(SUBS_ID).first()
        assertTrue(receivedWordsCards.size == CORRECT_ARRAY_SIZE)
        assertEquals(wordCardsMock[0].originalWord, receivedAllWordsCards[0].originalWord)

        //updateWordCard
        wordCardMock2.originalWord = MODIFIED_NAME
        vocabulatorDao.updateWordCard(wordCardMock2)
        val receivedModWordsCard: List<WordCard> =
            vocabulatorDao.getSubtitlesWordsCards(SUBS_ID_TWO).first()
        assertTrue(receivedModWordsCard.size == CORRECT_ARRAY_SIZE)
        assertEquals(wordCardMock2.originalWord, receivedModWordsCard[0].originalWord)

        //updateWordCards
        wordCardsMock[0].originalWord = MODIFIED_NAME
        vocabulatorDao.updateWordCards(wordCardsMock)
        val receivedModWordsCards: List<WordCard> =
            vocabulatorDao.getSubtitlesWordsCards(SUBS_ID).first()
        assertTrue(receivedModWordsCards.size == CORRECT_ARRAY_SIZE)
        assertEquals(wordCardsMock[0].originalWord, receivedModWordsCards[0].originalWord)

        //deleteWordCard
        vocabulatorDao.deleteWordCard(wordCardMock2)
        val receivedAllWordsCardsAfterDel: List<WordCard> =
            vocabulatorDao.getAllWordsCards().first()
        assertTrue(receivedAllWordsCardsAfterDel.size == CORRECT_ARRAY_SIZE)
        assertEquals(wordCardsMock[0].originalWord, receivedAllWordsCardsAfterDel[0].originalWord)

        //deleteWordCards
        vocabulatorDao.deleteWordCards(wordCardsMock)
        val receivedAllWordsCardsAfterFullDel: List<WordCard> =
            vocabulatorDao.getAllWordsCards().first()
        assertTrue(receivedAllWordsCardsAfterFullDel.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun commonWordsTest() = runTest {
        vocabulatorDao.insertCommonWords(commonWordsMock)
        vocabulatorDao.saveCommonWord(commonWordMock)

        var commonWords = vocabulatorDao.getCommonWords(LANG_ID)
        assertEquals(CORRECT_ARRAY_SIZE, commonWords.size)
        commonWords = vocabulatorDao.getCommonWords(LANG_ID_TWO)
        assertEquals(CORRECT_ARRAY_SIZE, commonWords.size)

        commonWordMock.word = MODIFIED_NAME
        vocabulatorDao.updateCommonWord(commonWordMock)
        commonWords = vocabulatorDao.getCommonWords(LANG_ID_TWO)
        assertEquals(commonWordMock.word, commonWords[0].word)

        vocabulatorDao.deleteCommonWords(commonWordsMock)
        commonWords = vocabulatorDao.getCommonWords(LANG_ID)
        assertEquals(0, commonWords.size)
    }
}