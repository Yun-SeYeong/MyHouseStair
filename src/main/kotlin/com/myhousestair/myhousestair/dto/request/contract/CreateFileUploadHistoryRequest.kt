package com.myhousestair.myhousestair.dto.request.contract

import jakarta.validation.constraints.NotBlank

data class CreateFileUploadHistoryRequest (
    @field:NotBlank(message = "contractId required value")
    val contractId: String,
    @field:NotBlank(message = "historyId required value")
    val fileType: String,
    @field:NotBlank(message = "description required value")
    val description: String
)