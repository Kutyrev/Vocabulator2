package com.github.kutyrev.vocabulator.model

import androidx.annotation.StringRes
import com.github.kutyrev.vocabulator.R

enum class Language(@StringRes val fullNameResource: Int) {
    EN(R.string.en_lang_fullname),
    FR(R.string.fr_lang_fullname),
    RU(R.string.ru_lang_fullname),
    IT(R.string.it_lang_fullname)
}
