package com.susess.cv360.validations

object ValidationRules {
    val SECURE_PASSWORD = ValidationRule(
        regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])(?!.*(.).*\\1).{8,}\$"),
        errorMessage = "Debe tener al menos 8 caracteres alfanumericos."
    )

    val VALID_EMAIL = ValidationRule(
        regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"),
        errorMessage = "Debes ingresar un correo electronico valido."
    )

    val NOT_EMPTY = ValidationRule(
        regex = Regex("^(?!\\s*$).+"), // Al menos un carácter no vacío
        errorMessage = "Este campo es obligatorio."
    )
}