package com.myhousestair.myhousestair.service

import com.myhousestair.myhousestair.domain.Contract
import com.myhousestair.myhousestair.domain.ContractHistory
import com.myhousestair.myhousestair.domain.ContractInventory
import com.myhousestair.myhousestair.domain.Member
import com.myhousestair.myhousestair.domain.common.ContractHistoryType
import com.myhousestair.myhousestair.domain.common.ContractRole
import com.myhousestair.myhousestair.domain.common.ContractStatus
import com.myhousestair.myhousestair.dto.request.contract.CreateContractRequest
import com.myhousestair.myhousestair.dto.request.contract.CreateFileUploadHistoryRequest
import com.myhousestair.myhousestair.dto.request.contract.CreateSpecialContractRequest
import com.myhousestair.myhousestair.dto.request.contract.JoinContractRequest
import com.myhousestair.myhousestair.dto.response.common.CommonResponse
import com.myhousestair.myhousestair.dto.response.common.PrimaryKeyResponse
import com.myhousestair.myhousestair.dto.response.contract.*
import com.myhousestair.myhousestair.exception.BadRequestException
import com.myhousestair.myhousestair.repository.ContractHistoryRepository
import com.myhousestair.myhousestair.repository.ContractInventoryRepository
import com.myhousestair.myhousestair.repository.ContractRepository
import com.myhousestair.myhousestair.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
@Transactional
class ContractService(
    private val memberRepository: MemberRepository,
    private val contractInventoryRepository: ContractInventoryRepository,
    private val contractRepository: ContractRepository,
    private val contractEventService: ContractEventService,
    private val contractHistoryRepository: ContractHistoryRepository,
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket-name}")
    private val bucketName: String,
) {

    fun createContract(createContractRequest: CreateContractRequest): CommonResponse<PrimaryKeyResponse> {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 생성
        val contract = createContractRequest.toContract()

        // 계약 인벤토리에 추가
        contractInventoryRepository.save(
            ContractInventory(
                member = memberEntity,
                contract = contract,
                contractRole = createContractRequest.contractRole!!
            )
        )

        return CommonResponse(PrimaryKeyResponse(contract.id.toString()))
    }

    fun joinContract(joinContractRequest: JoinContractRequest): CommonResponse<PrimaryKeyResponse> {
        // 유저 조회
        val memberEntity = getMemberEntity()

        val contractId = UUID.fromString(joinContractRequest.contractId)

        // 계약 조회
        val contract = contractRepository.findById(contractId)
            .orElseThrow { throw BadRequestException("Cannot find contract.") }

        // 이미 가입한 계약인지 확인
        contractInventoryRepository.findByMemberAndContractId(memberEntity, contractId)
            .ifPresent { throw BadRequestException("Already joined contract.") }

        // ContractRole이 비어있는지 확인
        contractInventoryRepository.findByContractIdAndContractRole(
            contractId,
            joinContractRequest.contractRole!!
        )
            .ifPresent { throw BadRequestException("Already joined contract.") }

        // 계약 인벤토리에 추가
        contractInventoryRepository.save(
            ContractInventory(
                member = memberEntity,
                contract = contract,
                contractRole = joinContractRequest.contractRole
            )
        )

        return CommonResponse(PrimaryKeyResponse(contract.id.toString()))
    }

    fun getMyContracts(pageable: Pageable): Page<ContractResponse> {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        return contractInventoryRepository.findByMemberOrderByContractCreatedAtDesc(
            memberEntity,
            pageable
        ).map { inventory ->
            ContractResponse.of(
                inventory.contract,
                inventory.contractRole,
                getContractStatus(inventory.contract)
            )
        }
    }

    fun getContractDetail(contractId: String): ContractDetailResponse {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        return ContractDetailResponse.of(
            contractInventory.contract,
            contractInventory.contractRole,
            getContractStatus(contractInventory.contract)
        )
    }

    fun checkHistory(contractId: String, historyId: String) {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // Step 완료
        val history = contractInventory.contract.contractSteps
            .flatMap { it.contractHistories }
            .find {
                it.id == UUID.fromString(historyId)
                        && it.type == ContractHistoryType.CHECK
            }
            ?: throw BadRequestException("Cannot find history.")

        history.checkStep()

        fetchUpdateAt(contractInventory.contract)

        contractEventService.publishEvent(
            ContractEvent(
                contractId = contractId,
                eventType = ContractEventType.ONCHANGED
            )
        )
    }

    fun uploadFile(contractId: String, historyId: String, file: MultipartFile) {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // Step 완료
        val history = contractInventory.contract.contractSteps
            .flatMap { it.contractHistories }
            .find {
                it.id == UUID.fromString(historyId)
                        && it.type == ContractHistoryType.FILE
            }
            ?: throw BadRequestException("Cannot find history.")

        // S3 업로드
        val fileKey = history.id.toString() + "-" + file.originalFilename
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .metadata(
                    mapOf(
                        "Content-Type" to file.contentType,
                        "Content-Length" to file.size.toString()
                    )
                ).build(),
            RequestBody.fromInputStream(file.inputStream, file.size)
        )
        history.uploadFile(fileKey)

        contractEventService.publishEvent(
            ContractEvent(
                contractId = contractId,
                eventType = ContractEventType.ONCHANGED
            )
        )
    }

    fun downloadFile(
        contractId: String,
        historyId: String
    ): Pair<String, ResponseInputStream<GetObjectResponse>> {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // Step 완료
        val history = contractInventory.contract.contractSteps
            .flatMap { it.contractHistories }
            .find { it.id == UUID.fromString(historyId) }
            ?: throw BadRequestException("Cannot find history.")

        // S3 다운로드
        return Pair(
            history.fileURL ?: historyId,
            s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(history.fileURL)
                    .build()
            )
        )
    }

    fun inputText(contractId: String, historyId: String, textInput: String) {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // Step 완료
        val history = (contractInventory.contract.contractSteps
            .flatMap { it.contractHistories }
            .find {
                it.id == UUID.fromString(historyId)
                        && it.type == ContractHistoryType.TEXT
            }
            ?: throw BadRequestException("Cannot find history."))

        history.inputText(textInput)

        contractEventService.publishEvent(
            ContractEvent(
                contractId = contractId,
                eventType = ContractEventType.ONCHANGED
            )
        )
    }

    fun createFileUploadHistory(createFileUploadHistoryRequest: CreateFileUploadHistoryRequest) {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(createFileUploadHistoryRequest.contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // 계약서에 파일 업로드 요청 추가
        val step = contractInventory.contract.contractSteps
            .find { it.status == getContractStatus(contractInventory.contract) }

        step?.addContractHistory(
            ContractHistory(
                isDefault = false,
                title = "${createFileUploadHistoryRequest.fileType} 업로드 요청",
                description = createFileUploadHistoryRequest.description,
                type = ContractHistoryType.FILE,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf(createFileUploadHistoryRequest.fileType)
            )
        )
        contractRepository.flush()

        contractEventService.publishEvent(
            ContractEvent(
                contractId = createFileUploadHistoryRequest.contractId,
                eventType = ContractEventType.ONCHANGED
            )
        )
    }

    fun createSpecialContractHistory(createSpecialContractRequest: CreateSpecialContractRequest) {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 계약 조회
        val contractInventory = contractInventoryRepository.findByMemberAndContractId(
            memberEntity,
            UUID.fromString(createSpecialContractRequest.contractId)
        ).orElseThrow { throw BadRequestException("Cannot find contract.") }

        // 계약서에 특약사항 요청 추가
        val step = contractInventory.contract.contractSteps
            .find { it.status == getContractStatus(contractInventory.contract) }

        step?.addContractHistory(
            ContractHistory(
                isDefault = false,
                title = "특약 추가 요청",
                description = "특약 사항을 추가한 계약서를 다시 업로드 해주세요",
                type = ContractHistoryType.TEXT,
                isCompleted = true,
                fileURL = null,
                textInput = createSpecialContractRequest.description,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("특약")
            )
        )
        contractRepository.flush()

        step?.addContractHistory(
            ContractHistory(
                isDefault = false,
                title = "계약서 업로드 요청",
                description = "계약서를 다시 업로드 해주세요",
                type = ContractHistoryType.FILE,
                isCompleted = false,
                fileURL = null,
                textInput = null,
                verifiedBy = ContractRole.LANDLORD,
                historyTags = listOf("계약서")
            )
        )
        contractRepository.flush()

        contractEventService.publishEvent(
            ContractEvent(
                contractId = createSpecialContractRequest.contractId,
                eventType = ContractEventType.ONCHANGED
            )
        )
    }

    fun getFileHistories(keyword: String): Any {
        // 유저 조회
        val memberEntity = getMemberEntity()

        // 모든 파일 히스토리 조회
        val histories = contractInventoryRepository.findByMember(
            memberEntity
        ).flatMap { inventory ->
            inventory.contract.contractSteps
        }.flatMap { step ->
            step.contractHistories
        }.filter {
            it.type == ContractHistoryType.FILE &&
                    (it.title.contains(keyword) ||
                            it.description.contains(keyword) ||
                            it.historyTags.any { tag -> tag.contains(keyword) })
        }.map {
            val contract = it.contractStep?.contract ?: throw BadRequestException("Cannot find contract.")
            ArchiveFileResponse(
                contractId = contract.id.toString(),
                historyId = it.id.toString(),
                address = contract.address,
                addressDetail = contract.addressDetail,
                fileKey = it.fileURL ?: "",
                historyTags = it.historyTags,
                updatedAt = it.updatedAt!!
            )
        }

        return ResponseEntity.ok(CommonResponse(histories))
    }

    private fun getMemberEntity(): Member {
        val member = (SecurityContextHolder.getContext().authentication.principal as Member)
        val memberEntity = memberRepository.findByEmail(member.email)
            .orElseThrow { throw BadRequestException("Cannot find user.") }
        return memberEntity
    }

    private fun fetchUpdateAt(contract: Contract) {
        contract.contractSteps
            .filter { it.status >= getContractStatus(contract) }
            .flatMap {
                it.update()
                it.contractHistories
            }
            .filter { !it.isCompleted }
            .forEach(ContractHistory::update)
    }

    private fun getContractStatus(contract: Contract) = contract.contractSteps
        .sortedBy { it.createdAt }
        .find { step ->
            step.contractHistories.find { !it.isCompleted } != null
        }?.status ?: ContractStatus.COMPLETED
}