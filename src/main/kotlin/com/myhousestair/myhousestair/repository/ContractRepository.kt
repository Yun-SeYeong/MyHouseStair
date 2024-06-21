package com.myhousestair.myhousestair.repository

import com.myhousestair.myhousestair.domain.Contract
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractRepository: JpaRepository<Contract, UUID>