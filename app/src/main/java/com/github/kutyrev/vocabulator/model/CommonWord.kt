package com.github.kutyrev.vocabulator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common_words")
data class CommonWord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var languageId: Int,
    var word: String
)
