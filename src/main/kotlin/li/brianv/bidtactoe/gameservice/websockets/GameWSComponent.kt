package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.GameReadyMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class GameWSComponent(val messagingTemplate: SimpMessageSendingOperations) {
    val logger = LoggerFactory.getLogger(GameWSComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: String, playerOneId: String, playerTwoId: String) {
        logger.info("gameReadyUpdate() gameIndex: $gameIndex, playerOneId: $playerOneId, playerTwoId: $playerTwoId")
        messagingTemplate.convertAndSend("/topic/public", GameReadyMessage(gameIndex, playerOneId, playerTwoId))
    }
}