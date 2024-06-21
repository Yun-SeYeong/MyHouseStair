package com.myhousestair.myhousestair.dto.request.contract

import com.myhousestair.myhousestair.domain.Contract
import com.myhousestair.myhousestair.domain.common.ContractRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateContractRequest (
    @field:NotBlank(message = "address required value")
    val address: String?,
    @field:NotBlank(message = "addressDetail is required value")
    val addressDetail: String?,
    @field:NotNull(message = "contractRole is required value")
    val contractRole: ContractRole?
) {
    fun toContract() = Contract(
        address = address!!,
        addressDetail = addressDetail!!
    )
}