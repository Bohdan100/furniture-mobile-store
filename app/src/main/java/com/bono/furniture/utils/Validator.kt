package com.bono.furniture.utils

import android.util.Patterns

object Validator {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.split("@")[0].isNotEmpty() &&
                email.split("@")[1].length >= 2
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 4 && password.any { it.isLetter() } && password.any { it.isDigit() }
    }
}