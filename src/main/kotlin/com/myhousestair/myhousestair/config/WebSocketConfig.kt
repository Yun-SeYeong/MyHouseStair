package com.myhousestair.myhousestair.config

import com.myhousestair.myhousestair.controller.ContractWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val contractWebSocketHandler: ContractWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(contractWebSocketHandler, "/contract-event/{contractId}")
            .setAllowedOrigins("*")
    }
}