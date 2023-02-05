package com.github.kutyrev.vocabulator.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabulatorDao {
    //Getters

    @Query("SELECT * FROM common_words WHERE languageId = :languageId")
    fun getCommonWords(languageId: Int) : List<CommonWord>

    @Query("SELECT * FROM subtitles")
    fun getSubtitlesList() : Flow<List<SubtitlesUnit>>

    @Query("SELECT * FROM words_cards WHERE subtitleId = :subtitleId")
    fun getSubtitlesWordsCards(subtitleId: Int) : Flow<List<WordCard>>

    //Modifying subtitles

    @Insert
    fun saveSubtitlesInfo(subtitlesUnit: SubtitlesUnit)

    @Update
    fun updateSubtitlesInfo(subtitlesUnit: SubtitlesUnit)

    @Delete
    fun deleteSubtitles(subtitlesUnit: SubtitlesUnit)

    //Modifying common words

    @Insert
    fun saveCommonWord(commonWord: CommonWord)

    @Update
    fun updateCommonWord(commonWord: CommonWord)

    @Delete
    fun deleteCommonWord(commonWord: CommonWord)

    //Modifying words cards

    @Insert
    fun saveWordCard(wordCard: WordCard)

    @Update
    fun updateWordCard(wordCard: WordCard)

    @Delete
    fun deleteWordCard(wordCard: WordCard)
}
