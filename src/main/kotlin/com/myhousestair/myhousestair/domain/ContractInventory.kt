package com.myhousestair.myhousestair.domain

import com.myhousestair.myhousestair.domain.common.ContractRole
import com.myhousestair.myhousestair.domain.common.PrimaryKeyEntity
import jakarta.persistence.*

@Entity
@Table(name = "CONTRACT_INVENTORY_TABLE")
class ContractInventory(
    contract: Contract,
    member: Member,
    contractRole: ContractRole
) : PrimaryKeyEntity() {

    init {
        member.addContractInventory(this)
        contract.addContractInventory(this)
    }

    @ManyToOne(optional = false, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var contract: Contract = contract
        protected set

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var member: Member = member
        protected set

    @Column
    @Enumerated(EnumType.STRING)
    var contractRole: ContractRole = contractRole
        protected set
}