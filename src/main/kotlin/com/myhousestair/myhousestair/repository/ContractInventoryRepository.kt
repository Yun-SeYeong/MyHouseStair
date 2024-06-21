package com.myhousestair.myhousestair.repository

import com.myhousestair.myhousestair.domain.ContractInventory
import com.myhousestair.myhousestair.domain.Member
import com.myhousestair.myhousestair.domain.common.ContractRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ContractInventoryRepository : JpaRepository<ContractInventory, UUID> {
    fun findByMemberOrderByContractCreatedAtDesc(member: Member, pageable: Pageable): Page<ContractInventory>

    @Query(
        "SELECT ci FROM ContractInventory ci " +
                "join fetch ci.contract c " +
                "join fetch c._contractSteps cs " +
                "join fetch cs._contractHistories ch " +
                "where ci.member = :member and c.id = :contractId "
    )
    fun findByMemberAndContractId(
        member: Member,
        contractId: UUID
    ): Optional<ContractInventory>

    @Query(
        "SELECT ci FROM ContractInventory ci " +
                "join fetch ci.contract c " +
                "join fetch c._contractSteps cs " +
                "join fetch cs._contractHistories ch " +
                "where ci.member = :member " +
                "order by c.createdAt desc"
    )
    fun findByMember(member: Member): List<ContractInventory>

    @Query(
        "SELECT ci FROM ContractInventory ci " +
                "join fetch ci.contract c " +
                "join fetch c._contractSteps cs " +
                "join fetch cs._contractHistories ch " +
                "where c.id = :contractId and ci.contractRole = :contractRole"
    )
    fun findByContractIdAndContractRole(
        contractId: UUID,
        contractRole: ContractRole
    ): Optional<ContractInventory>
}