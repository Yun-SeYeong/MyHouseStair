package com.myhousestair.myhousestair.dto.response.contract

import com.myhousestair.myhousestair.domain.Contract
import com.myhousestair.myhousestair.domain.common.ContractRole
import com.myhousestair.myhousestair.domain.common.ContractStatus
import java.time.LocalDateTime

data class ContractDetailResponse(
    val id: String?,
    val contractRole: ContractRole,
    val address: String?,
    val addressDetail: String?,
    val status: ContractStatus,
    val steps: List<ContractStepResponse>,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun of(
            contract: Contract,
            contractRole: ContractRole,
            contractStatus: ContractStatus
        ): ContractDetailResponse {
            return ContractDetailResponse(
                id = contract.id.toString(),
                contractRole = contractRole,
                address = contract.address,
                addressDetail = contract.addressDetail,
                status = contractStatus,
                createdAt = contract.createdAt,
                steps = contract.contractSteps
                    .sortedBy { it.createdAt }
                    .map {
                        ContractStepResponse.of(it)
                    }
            )
        }
    }
}
