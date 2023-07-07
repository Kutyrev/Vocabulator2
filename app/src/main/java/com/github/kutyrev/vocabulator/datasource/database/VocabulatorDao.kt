package com.github.kutyrev.vocabulator.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import com.github.kutyrev.vocabulator.model.WordsCount
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabulatorDao {
    //Getters

    @Query("SELECT * FROM common_words WHERE languageId = :languageId ORDER BY word")
    fun getCommonWords(languageId: Int) : List<CommonWord>

    @Query("SELECT * FROM subtitles")
    fun getSubtitlesList() : Flow<List<SubtitlesUnit>>

    @Query("SELECT * FROM subtitles WHERE id = :id")
    fun getSubtitleUnit(id: Int) : SubtitlesUnit

    @Query("SELECT * FROM words_cards WHERE subtitleId = :subtitleId")
    fun getSubtitlesWordsCards(subtitleId: Int) : Flow<List<WordCard>>

    @Query("SELECT * FROM words_cards")
    fun getAllWordsCards() : Flow<List<WordCard>>

    @Query("SELECT subtitleId AS subId, COUNT(id) AS wordsCount FROM words_cards GROUP BY subtitleId")
    fun getWordsCount() : Flow<List<WordsCount>>

    //Modifying subtitles

    @Insert
    fun insertSubtitlesInfo(subtitlesUnit: SubtitlesUnit) : Long

    @Update
    fun updateSubtitlesInfo(subtitlesUnit: SubtitlesUnit)

    @Delete
    fun deleteSubtitles(subtitlesUnit: SubtitlesUnit)

    //Modifying common words

    @Insert
    fun saveCommonWord(commonWord: CommonWord)

    @Insert
    fun insertCommonWords(commonWords: List<CommonWord>)

    @Update
    fun updateCommonWord(commonWord: CommonWord)

    @Delete
    fun deleteCommonWords(commonWords: List<CommonWord>)

    //Modifying words cards

    @Insert
    fun insertWordCard(wordCard: WordCard)

    @Insert
    fun insertWordCards(wordCards: List<WordCard>)

    @Update
    fun updateWordCard(wordCard: WordCard)

    @Update
    fun updateWordCards(wordCards: List<WordCard>)

    @Delete
    fun deleteWordCard(wordCard: WordCard)

    @Delete
    fun deleteWordCards(wordCards: List<WordCard>)
}
