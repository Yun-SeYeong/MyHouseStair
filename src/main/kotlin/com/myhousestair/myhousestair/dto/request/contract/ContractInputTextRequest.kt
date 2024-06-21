package com.myhousestair.myhousestair.dto.request.contract

import jakarta.validation.constraints.NotBlank

data class ContractInputTextRequest (
    @field:NotBlank(message = "textInput is required value")
    val textInput: String
)