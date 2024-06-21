package com.myhousestair.myhousestair.domain

import com.myhousestair.myhousestair.domain.common.PrimaryKeyEntity
import com.myhousestair.myhousestair.domain.common.ContractHistoryType
import com.myhousestair.myhousestair.domain.common.ContractRole
import com.myhousestair.myhousestair.util.JsonToListConverter
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "CONTRACT_HISTORY_TABLE")
class ContractHistory(
    isDefault: Boolean,
    title: String,
    description: String,
    type: ContractHistoryType,
    isCompleted: Boolean,
    fileURL: String?,
    textInput: String?,
    verifiedBy: ContractRole,
    historyTags: List<String>
) : PrimaryKeyEntity(){

    @Column
    var isDefault: Boolean = isDefault
        protected set

    @Column
    var title: String = title
        protected set

    @Column
    var description: String = description
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var type: ContractHistoryType = type
        protected set

    @Column
    var isCompleted: Boolean = isCompleted
        protected set

    @Column
    var fileURL: String? = fileURL
        protected set

    @Column
    var textInput: String? = textInput
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var verifiedBy: ContractRole = verifiedBy
        protected set

    @Column
    @Convert(converter = JsonToListConverter::class)
    var historyTags: List<String> = historyTags
        protected set

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var contractStep: ContractStep? = null
        protected set

    fun setContract(contractStep: ContractStep) {
        this.contractStep = contractStep
    }

    fun checkStep() {
        this.isCompleted = true
    }

    fun uploadFile(fileURL: String) {
        this.fileURL = fileURL
        this.isCompleted = true
    }

    fun inputText(textInput: String) {
        this.textInput = textInput
        this.isCompleted = true
    }

    fun update() {
        this.updatedAt = LocalDateTime.now()
    }
}