package com.susess.cv360.validations

data class ValidationRule(
    val regex: Regex,
    val errorMessage: String
)
