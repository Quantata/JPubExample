package com.quantata.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answer: Boolean, var isCorrect: Boolean = false, var isAnswered: Boolean = false)