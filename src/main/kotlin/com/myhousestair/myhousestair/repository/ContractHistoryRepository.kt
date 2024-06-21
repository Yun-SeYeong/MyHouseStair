package com.myhousestair.myhousestair.repository

import com.myhousestair.myhousestair.domain.ContractHistory
import org.springframework.data.jpa.repository.JpaRepository

interface ContractHistoryRepository : JpaRepository<ContractHistory, Long> {
}