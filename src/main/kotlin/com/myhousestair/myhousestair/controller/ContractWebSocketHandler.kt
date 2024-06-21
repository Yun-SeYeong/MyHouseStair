package com.myhousestair.myhousestair.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.myhousestair.myhousestair.service.ContractEventService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriTemplate
import reactor.core.Disposable

@Component
class ContractWebSocketHandler(
    private val contractEventService: ContractEventService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        var disposable: Disposable? = null

        val uri = session.uri ?: return session.close()
        println("uri.path = ${uri.path}")

        val match = UriTemplate("/contract-event/{contractId}")
            .match(uri.path)

        val flux = match["contractId"]?.let { contractId ->
            contractEventService.getEventFlux(contractId)
        } ?: return session.close()

        disposable = flux.subscribe {
            try {
                if (session.isOpen) {
                    println("[EVENT] ONCHANGED ${it.contractId}")
                    session.sendMessage(TextMessage(objectMapper.writeValueAsString(it)))
                } else {
                    disposable?.dispose()
                    println("session is closed")
                }
            } catch (e: Exception) {
                session.close(CloseStatus.SERVER_ERROR)
                disposable?.dispose()
            }
        }
    }
}