package com.github.kutyrev.vocabulator.features.commons.model

import com.github.kutyrev.vocabulator.model.CommonWord

class EditableCommonWord(
    id: Int = 0,
    languageId: Int,
    word: String
) : CommonWord(id, languageId, word) {

    var checked = true

    fun copy(
        id: Int = this.id,
        languageId: Int = this.languageId,
        word: String = this.word,
        checked: Boolean = this.checked,
    ): EditableCommonWord {
        val newWordInstance = EditableCommonWord(id, languageId, word)
        newWordInstance.checked = checked
        return newWordInstance
    }
}
