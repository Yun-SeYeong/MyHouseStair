package com.myhousestair.myhousestair.dto.response.contract

import java.time.LocalDateTime


data class ArchiveFileResponse (
    val contractId: String,
    val historyId: String,
    val address: String,
    val addressDetail: String,
    val fileKey: String,
    val historyTags: List<String>,
    val updatedAt: LocalDateTime
)