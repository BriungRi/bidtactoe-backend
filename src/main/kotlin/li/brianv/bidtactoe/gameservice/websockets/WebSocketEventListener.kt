package li.brianv.bidtactoe.gameservice.websockets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener(val messagingTemplate: SimpMessageSendingOperations) {

    val logger: Logger = LoggerFactory.getLogger(WebSocketEventListener::class.java.simpleName)

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        logger.info("Received a new web socket connection: $event")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)

        val username = headerAccessor.sessionAttributes["username"] as String
        if (username != null) {
            logger.info("User Disconnected : " + username)

            messagingTemplate.convertAndSend("/topic/public", Greeting("Hello"))
        }
    }
}