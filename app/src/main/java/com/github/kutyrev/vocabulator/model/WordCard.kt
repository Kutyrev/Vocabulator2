package com.github.kutyrev.vocabulator.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

val EMPTY_CARD = WordCard( -1)

@Entity(
    tableName = "words_cards",
    foreignKeys = [ForeignKey(
        entity = SubtitlesUnit::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("subtitleId"),
        onDelete = ForeignKey.CASCADE
    )],
    /* A "CASCADE" action propagates the delete or update operation on the parent key to each
    dependent child key. For onDelete() action, this means that each row in the child entity
    that was associated with the deleted parent row is also deleted.*/
    indices = [Index("subtitleId")]
)
data class WordCard(
    var subtitleId: Int,
    var originalWord: String = "",
    var translatedWord: String = ""
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
