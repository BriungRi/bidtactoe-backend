package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.BidsReadyMessage
import li.brianv.bidtactoe.gameservice.model.GameReadyMessage
import li.brianv.bidtactoe.gameservice.model.MoveUpdateMessage
import li.brianv.bidtactoe.gameservice.model.WinnerUpdateMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class GameWSComponent(val messagingTemplate: SimpMessageSendingOperations) {
    val logger = LoggerFactory.getLogger(GameWSComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) {
        logger.info("gameReadyUpdate() gameIndex: $gameIndex, playerOneId: $playerOneUsername, playerTwoUsername: $playerTwoUsername")
        messagingTemplate.convertAndSend("/topic/public/$playerOneUsername",
                GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
        messagingTemplate.convertAndSend("/topic/public/$playerTwoUsername",
                GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
    }

    fun bidsCompletedUpdate(bidWinnerId: String, biddingPower: Int, username: String) {
        logger.info("bidsCompletedUpdate() bidWinnerId: $bidWinnerId, biddingPower: $biddingPower")
        messagingTemplate.convertAndSend("/topic/public/$username",
                BidsReadyMessage(bidWinnerId, biddingPower))
    }

    fun moveUpdate(cells: String, vararg usernames: String) {
        logger.info("moveUpdate() cells: $cells")
        for(username in usernames) {
            messagingTemplate.convertAndSend("/topic/public/$username", MoveUpdateMessage(cells))
        }
    }

    fun victoryUpdate(winnerId: String, vararg usernames: String) {
        logger.info("victoryUpdate() winnerId: $winnerId")
        for(username in usernames) {
            messagingTemplate.convertAndSend("/topic/public/$username", WinnerUpdateMessage(winnerId))
        }
    }
}