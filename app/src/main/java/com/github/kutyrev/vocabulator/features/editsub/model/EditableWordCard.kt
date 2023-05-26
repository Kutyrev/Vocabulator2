package com.github.kutyrev.vocabulator.features.editsub.model

import com.github.kutyrev.vocabulator.model.WordCard

class EditableWordCard(
    id: Int,
    subtitleId: Int,
    originalWord: String = "",
    translatedWord: String = "",
    quantity: Int = 0,
    phrase: String = ""
) :
    WordCard(id, subtitleId, originalWord, translatedWord, quantity, phrase) {
    var checked = true
    var changed = false

    fun copy(
        id: Int = this.id,
        subtitleId: Int = this.subtitleId,
        originalWord: String = this.originalWord,
        translatedWord: String = this.translatedWord,
        quantity: Int = this.quantity,
        phrase: String = this.phrase,
        checked: Boolean = this.checked,
        changed: Boolean = this.changed
    ): EditableWordCard {
        val newCardInstance =
            EditableWordCard(id, subtitleId, originalWord, translatedWord, quantity, phrase)
        newCardInstance.checked = checked
        newCardInstance.changed = changed
        return newCardInstance
    }
}
