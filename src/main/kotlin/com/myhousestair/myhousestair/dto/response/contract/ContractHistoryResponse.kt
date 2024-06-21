package com.myhousestair.myhousestair.dto.response.contract

import com.myhousestair.myhousestair.domain.ContractHistory
import com.myhousestair.myhousestair.domain.common.ContractHistoryType
import com.myhousestair.myhousestair.domain.common.ContractRole
import java.time.LocalDateTime

data class ContractHistoryResponse (
    val id: String,
    val isDefault: Boolean,
    val title: String,
    val description: String,
    val type: ContractHistoryType,
    val isCompleted: Boolean,
    val fileURL: String?,
    val textInput: String?,
    val verifiedBy: ContractRole,
    val historyTags: List<String>,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(
            contractHistory: ContractHistory
        ): ContractHistoryResponse {
            return ContractHistoryResponse(
                id = contractHistory.id.toString(),
                isDefault = contractHistory.isDefault,
                title = contractHistory.title,
                description = contractHistory.description,
                type = contractHistory.type,
                isCompleted = contractHistory.isCompleted,
                fileURL = contractHistory.fileURL,
                textInput = contractHistory.textInput,
                verifiedBy = contractHistory.verifiedBy,
                historyTags = contractHistory.historyTags,
                updatedAt = contractHistory.updatedAt
            )
        }
    }
}
