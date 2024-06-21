package com.myhousestair.myhousestair.dto.response.contract

import com.myhousestair.myhousestair.domain.ContractStep
import com.myhousestair.myhousestair.domain.common.ContractStatus
import java.time.LocalDateTime

data class ContractStepResponse(
    val status: ContractStatus,
    val requestEnabled: Boolean,
    val contractHistories: List<ContractHistoryResponse>,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun of(
            contractStep: ContractStep
        ): ContractStepResponse {
            return ContractStepResponse(
                status = contractStep.status,
                requestEnabled = contractStep.requestEnabled,
                contractHistories = contractStep.contractHistories
                    .sortedWith(compareBy({ it.isDefault }, { it.createdAt }))
                    .map {
                        ContractHistoryResponse.of(it)
                    },
                createdAt = contractStep.updatedAt
            )
        }
    }
}