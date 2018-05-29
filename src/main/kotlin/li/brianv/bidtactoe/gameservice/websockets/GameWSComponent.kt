package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.messages.BidsReadyMessage
import li.brianv.bidtactoe.gameservice.model.messages.GameReadyMessage
import li.brianv.bidtactoe.gameservice.model.messages.MoveUpdateMessage
import li.brianv.bidtactoe.gameservice.model.messages.WinnerUpdateMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class GameWSComponent(val messagingTemplate: SimpMessageSendingOperations) {
    val logger = LoggerFactory.getLogger(GameWSComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String, username: String) {
        Thread {
            Thread.sleep(1000)
            logger.info("gameReadyUpdate() " +
                    "gameIndex: $gameIndex, " +
                    "playerOneUsername: $playerOneUsername, " +
                    "playerTwoUsername: $playerTwoUsername, " +
                    "username: $username")
            messagingTemplate.convertAndSend("/topic/public/$username",
                    GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
        }.run()
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