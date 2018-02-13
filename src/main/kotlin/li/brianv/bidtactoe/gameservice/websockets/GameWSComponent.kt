package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.GameReadyMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class GameWSComponent(val messagingTemplate: SimpMessageSendingOperations) {
    val logger = LoggerFactory.getLogger(GameWSComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: String, playerOneUsername: String, playerTwoUsername: String) {
        logger.info("gameReadyUpdate() gameIndex: $gameIndex, playerOneId: $playerOneUsername, playerTwoUsername: $playerTwoUsername")
        messagingTemplate.convertAndSend("/topic/public/$playerOneUsername",
                GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
        messagingTemplate.convertAndSend("/topic/public/$playerTwoUsername",
                GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
    }
}