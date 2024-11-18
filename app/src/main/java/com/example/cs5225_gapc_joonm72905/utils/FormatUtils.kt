package com.example.cs5225_gapc_joonm72905.utils

import java.util.regex.Pattern

object FormatUtils {
    fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return emailPattern.matcher(email).matches()
    }
}