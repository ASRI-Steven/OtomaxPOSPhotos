package com.example.otomaxposphotos.utils

object Utils {
    const val BASE_URL = "http://34.142.212.52:80"
    fun convertNumberThreeDots(number: String): String {
        val substring = number.substringBefore(".")
        val newNumber = "%,d".format(substring.toLong()).replace(",", ".")
        return newNumber
    }

    fun convertNumberToThreeDots(number: Long): String {
        val newNumber = "%,d".format(number).replace(",", ".")
        return newNumber.toString()
    }

    fun convertNumber(number: String): Long {
        val substring = number.substringBefore(".")
        return substring.toLong()
    }
}