package com.github.kutyrev.vocabulator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common_words")
open class CommonWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var languageId: Int,
    var word: String
)
