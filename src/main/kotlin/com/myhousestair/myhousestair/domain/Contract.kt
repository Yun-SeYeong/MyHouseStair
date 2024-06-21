package com.myhousestair.myhousestair.domain

import com.myhousestair.myhousestair.domain.common.PrimaryKeyEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.myhousestair.myhousestair.domain.common.ContractHistoryType
import com.myhousestair.myhousestair.domain.common.ContractRole
import com.myhousestair.myhousestair.domain.common.ContractStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "CONTRACT_TABLE")
class Contract(
    address: String,
    addressDetail: String
) : PrimaryKeyEntity() {

    @Column
    var address: String = address
        protected set

    @Column
    var addressDetail: String = addressDetail
        protected set

    @Column
    var expiredAt: LocalDateTime? = null
        protected set

    @JsonIgnore
    @OneToMany(mappedBy = "contract", cascade = [CascadeType.ALL])
    private val _contractSteps: MutableSet<ContractStep> = getDefaultSteps()
    val contractSteps: List<ContractStep>
        get() = _contractSteps.toList()

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contract")
    private val _contractInventories: MutableSet<ContractInventory> = mutableSetOf()
    val contractInventories: List<ContractInventory>
        get() = _contractInventories.toList()

    // 계약 초기 설정
    private fun getDefaultSteps() = mutableSetOf(
        getDefaultRoomCheckHistory(),
        getDefaultProvisionalContractHistory(),
        getDefaultContractHistory(),
        getDefaultCompleteHistory()
    )

    private fun getDefaultCompleteHistory() = ContractStep(
        status = ContractStatus.COMPLETED,
        requestEnabled = false,
        contract = this,
        contractHistories = mutableSetOf(
            ContractHistory(
                isDefault = true,
                title = "확정일자",
                description = "확정일자를 입력해주세요.",
                type = ContractHistoryType.TEXT,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LESSEE,
                historyTags = listOf("계약서")
            ),
        )
    )

    private fun getDefaultContractHistory() = ContractStep(
        status = ContractStatus.CONTRACT,
        requestEnabled = true,
        contract = this,
        contractHistories = mutableSetOf(
            ContractHistory(
                isDefault = false,
                title = "계약서 업로드 요청",
                description = "계약서를 업로드 해주세요.",
                type = ContractHistoryType.FILE,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("계약서")
            ),
            ContractHistory(
                isDefault = false,
                title = "계약금 입금 계좌 요청",
                description = "임차인이 계약금을 입금을 할 수 있도록 계좌 번호를 입력해주세요.",
                type = ContractHistoryType.TEXT,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("계약금입금계좌")
            ),
            ContractHistory(
                isDefault = true,
                title = "계약금 입금 확인 요청",
                description = "계약금 입금이 완료되면 확인 버튼을 눌려주세요",
                type = ContractHistoryType.CHECK,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LESSEE,
                historyTags = listOf()
            ),
            ContractHistory(
                isDefault = true,
                title = "계약금 입금 확인 요청",
                description = "계약금 입금이 완료되면 확인 버튼을 눌려주세요",
                type = ContractHistoryType.CHECK,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf()
            )
        )
    )

    private fun getDefaultProvisionalContractHistory() = ContractStep(
        status = ContractStatus.PROVISIONAL_CONTRACT,
        requestEnabled = true,
        contract = this,
        contractHistories = mutableSetOf(
            ContractHistory(
                isDefault = false,
                title = "등기부 등본 업로드 요청",
                description = "등기부등본을 업로드 해주세요.",
                type = ContractHistoryType.FILE,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("등기부등본")
            ),
            ContractHistory(
                isDefault = false,
                title = "가계약금 입금 계좌 요청",
                description = "임차인이 가계약금을 입금을 할 수 있도록 계좌 번호를 입력해주세요.",
                type = ContractHistoryType.TEXT,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("가계약금입금계좌")
            ),
            ContractHistory(
                isDefault = true,
                title = "가계약금 입금 확인 요청",
                description = "가계약금 입금이 완료되면 확인 버튼을 눌려주세요",
                type = ContractHistoryType.CHECK,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LESSEE,
                historyTags = listOf()
            ),
            ContractHistory(
                isDefault = true,
                title = "가계약금 입금 확인 요청",
                description = "가계약금 입금이 완료되면 확인 버튼을 눌려주세요",
                type = ContractHistoryType.CHECK,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf()
            )
        )
    )

    private fun getDefaultRoomCheckHistory() = ContractStep(
        status = ContractStatus.ROOM_CHECK,
        requestEnabled = false,
        contract = this,
        contractHistories = mutableSetOf(
            ContractHistory(
                isDefault = true,
                title = "방 확인 요청",
                description = "방을 확인합니다.",
                type = ContractHistoryType.CHECK,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LESSEE,
                historyTags = listOf()
            )
        )
    )

    fun addContractInventory(contractInventory: ContractInventory) {
        _contractInventories.add(contractInventory)
    }
}