package com.myhousestair.myhousestair.controller

import com.myhousestair.myhousestair.dto.request.contract.*
import com.myhousestair.myhousestair.service.ContractService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/contract")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
class ContractController(
    private val contractService: ContractService
) {

    @PostMapping
    fun createContract(
        @Valid @RequestBody createContractRequest: CreateContractRequest
    ) = contractService.createContract(createContractRequest)

    @GetMapping
    fun getContracts(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int
    ) = contractService.getMyContracts(PageRequest.of(page, size))

    @GetMapping("/fileHistories/{keyword}/search")
    fun getFileHistories(
        @PathVariable keyword: String
    ) = contractService.getFileHistories(keyword)

    @GetMapping("/{contractId}")
    fun getContractDetail(
        @PathVariable contractId: String
    ) = contractService.getContractDetail(contractId)

    @PostMapping("/{contractId}/history/{historyId}/check")
    fun checkHistory(
        @PathVariable contractId: String,
        @PathVariable historyId: String
    ) = contractService.checkHistory(contractId, historyId)

    @PostMapping(
        "/{contractId}/history/{historyId}/uploadFile",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFile(
        @PathVariable contractId: String,
        @PathVariable historyId: String,
        @RequestPart file: MultipartFile
    ) = contractService.uploadFile(contractId, historyId, file)

    @GetMapping("/{contractId}/history/{historyId}/downloadFile")
    fun downloadFile(
        @PathVariable contractId: String,
        @PathVariable historyId: String
    ): ResponseEntity<InputStreamResource> {
        val downloadFile = contractService.downloadFile(contractId, historyId)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${downloadFile.first}\""
            ).body(InputStreamResource(downloadFile.second))
    }

    @PostMapping("/{contractId}/history/{historyId}/inputText")
    fun inputText(
        @PathVariable contractId: String,
        @PathVariable historyId: String,
        @Valid @RequestBody contractInputTextRequest: ContractInputTextRequest
    ) = contractService.inputText(contractId, historyId, contractInputTextRequest.textInput)

    @PostMapping("/join")
    fun joinContract(
        @Valid @RequestBody joinContractRequest: JoinContractRequest
    ) = contractService.joinContract(joinContractRequest)

    @PostMapping("/createFileUploadHistory")
    fun createFileUploadHistory(
        @Valid @RequestBody createFileUploadHistoryRequest: CreateFileUploadHistoryRequest
    ) = contractService.createFileUploadHistory(createFileUploadHistoryRequest)

    @PostMapping("/createSpecialContract")
    fun createTextHistory(
        @Valid @RequestBody contractInputTextRequest: CreateSpecialContractRequest
    ) = contractService.createSpecialContractHistory(contractInputTextRequest)
}