package com.github.kutyrev.vocabulator.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "subtitles")
data class SubtitlesUnit(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    var origLangId: Int,
    var transLangId: Int
) {
    @Ignore
    val wordCards: MutableList<WordCard> = mutableListOf()
}
