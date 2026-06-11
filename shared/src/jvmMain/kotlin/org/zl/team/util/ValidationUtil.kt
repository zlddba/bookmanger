package org.zl.team.util

object ValidationUtil {

    fun isBlank(value: String?): Boolean = value.isNullOrBlank()

    fun isNotBlank(value: String?): Boolean = !value.isNullOrBlank()

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return email.matches(Regex("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
    }

    fun isValidPhone(phone: String?): Boolean {
        if (phone.isNullOrBlank()) return false
        return phone.matches(Regex("^[\\d\\-+() ]{7,20}$"))
    }

    fun isValidIsbn(isbn: String?): Boolean {
        if (isbn.isNullOrBlank()) return false
        val cleaned = isbn.replace("-", "").replace(" ", "")
        return cleaned.matches(Regex("^(97[89])?\\d{9}[\\dXx]$"))
    }

    fun isPositiveInt(value: String?): Boolean {
        val n = value?.toIntOrNull() ?: return false
        return n > 0
    }

    fun isNonNegativeInt(value: String?): Boolean {
        val n = value?.toIntOrNull() ?: return false
        return n >= 0
    }

    fun isPositiveDouble(value: String?): Boolean {
        val d = value?.toDoubleOrNull() ?: return false
        return d > 0
    }

    fun isNonNegativeDouble(value: String?): Boolean {
        val d = value?.toDoubleOrNull() ?: return false
        return d >= 0
    }
}
