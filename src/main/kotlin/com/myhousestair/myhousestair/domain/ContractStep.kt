package com.myhousestair.myhousestair.domain

import com.myhousestair.myhousestair.domain.common.ContractStatus
import com.myhousestair.myhousestair.domain.common.PrimaryKeyEntity
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.time.LocalDateTime

@Entity
@Table(name = "CONTRACT_STEP_TABLE")
class ContractStep(
    status: ContractStatus,
    requestEnabled: Boolean,
    contract: Contract,
    contractHistories: MutableSet<ContractHistory> = mutableSetOf()
) : PrimaryKeyEntity() {

    init {
        contractHistories.forEach { it.setContract(this) }
    }

    @Column
    @Enumerated(EnumType.STRING)
    var status: ContractStatus = status
        protected set

    @Column
    var requestEnabled: Boolean = requestEnabled
        protected set

    @OneToMany(mappedBy = "contractStep", cascade = [CascadeType.ALL])
    private val _contractHistories: MutableSet<ContractHistory> = contractHistories
    val contractHistories: List<ContractHistory>
        get() = _contractHistories.toList()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    var contract: Contract = contract
        protected set

    fun addContractHistory(contractHistory: ContractHistory) {
        _contractHistories.add(contractHistory)
        contractHistory.setContract(this)
    }

    fun update() {
        updatedAt = LocalDateTime.now()
    }
}