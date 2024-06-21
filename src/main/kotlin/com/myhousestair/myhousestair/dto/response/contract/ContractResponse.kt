package com.myhousestair.myhousestair.dto.response.contract

import com.myhousestair.myhousestair.domain.Contract
import com.myhousestair.myhousestair.domain.common.ContractStatus
import com.myhousestair.myhousestair.domain.common.ContractRole
import java.time.LocalDateTime

data class ContractResponse(
    val id: String?,
    val contractRole: ContractRole,
    val address: String?,
    val addressDetail: String?,
    val status: ContractStatus,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun of(
            contract: Contract,
            contractRole: ContractRole,
            contractStatus: ContractStatus
        ): ContractResponse {
            return ContractResponse(
                id = contract.id.toString(),
                contractRole = contractRole,
                address = contract.address,
                addressDetail = contract.addressDetail,
                status = contractStatus,
                createdAt = contract.createdAt
            )
        }
    }
}
