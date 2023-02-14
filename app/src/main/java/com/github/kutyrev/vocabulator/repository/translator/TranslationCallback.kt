package com.github.kutyrev.vocabulator.repository.translator

import com.github.kutyrev.vocabulator.model.WordCard

interface TranslationCallback {
    fun receiveTranslation(translatedWords: List<WordCard>)
}
