package com.github.kutyrev.vocabulator.model

val EMPTY_CARD = WordCard(0,0)

class WordCard(
    val id: Int,
    val subtitleId: Int,
    var originalWord: String = "",
    var translatedWord: String = ""
)
