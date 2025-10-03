package com.susess.cv360.helpers

import com.susess.cv360.validations.ValidationResult
import com.susess.cv360.validations.ValidationRule
import com.susess.cv360.validations.ValidationRules

object ValidateFieldsHelper {
    fun validateField(value: String, rules: List<ValidationRule>): ValidationResult {
        if(value.isBlank()){
            val emptyRule = rules.find { it == ValidationRules.NOT_EMPTY }
            if (emptyRule != null){
                return ValidationResult.Invalid(emptyRule.errorMessage)
            }
            return ValidationResult.Valid
        }

        for (rule in rules){
            if(rule == ValidationRules.NOT_EMPTY) continue

            if(!rule.regex.matches(value)){
                return ValidationResult.Invalid(rule.errorMessage)
            }
        }

        // Si todas las reglas pasan, es válido
        return ValidationResult.Valid
    }
}