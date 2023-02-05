package com.github.kutyrev.vocabulator.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.kutyrev.vocabulator.model.CommonWord
import com.github.kutyrev.vocabulator.model.SubtitlesUnit
import com.github.kutyrev.vocabulator.model.WordCard

@Database(entities = [CommonWord::class, SubtitlesUnit::class, WordCard::class], version = 1)
abstract class DatabaseSource : RoomDatabase() {
    abstract fun vocabulatorDao(): VocabulatorDao
}
