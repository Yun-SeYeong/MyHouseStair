package com.myhousestair.myhousestair.service

import com.myhousestair.myhousestair.dto.response.contract.ContractEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Service
@Transactional
class ContractEventService {
    private val sinkMap = mutableMapOf<String, Sinks.Many<ContractEvent>>()

    fun publishEvent(event: ContractEvent) {
        val sink = sinkMap.computeIfAbsent(event.contractId) {
            Sinks.many().multicast().onBackpressureBuffer()
        }
        sink.tryEmitNext(event)
    }

    fun getEventFlux(contractId: String): Flux<ContractEvent> {
        return sinkMap.computeIfAbsent(contractId) {
            Sinks.many().multicast().onBackpressureBuffer()
        }.asFlux()
    }
}