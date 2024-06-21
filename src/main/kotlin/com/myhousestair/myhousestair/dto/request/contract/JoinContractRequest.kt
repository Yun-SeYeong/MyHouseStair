package com.myhousestair.myhousestair.dto.request.contract

import com.myhousestair.myhousestair.domain.common.ContractRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class JoinContractRequest(
    @field:NotBlank(message = "contractId is required value")
    val contractId: String?,
    @field:NotNull(message = "contractRole is required value")
    val contractRole: ContractRole?
)
