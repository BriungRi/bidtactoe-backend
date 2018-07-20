package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.messages.BidsReadyMessage
import li.brianv.bidtactoe.gameservice.model.messages.GameReadyMessage
import li.brianv.bidtactoe.gameservice.model.messages.MoveUpdateMessage
import li.brianv.bidtactoe.gameservice.model.messages.WinnerUpdateMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class GameWSComponent(val messagingTemplate: SimpMessageSendingOperations) {
    val logger = LoggerFactory.getLogger(GameWSComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String, username: String) {
        thread(start = true) {
            Thread.sleep(1000)
            messagingTemplate.convertAndSend("/topic/public/$username",
                    GameReadyMessage(gameIndex, playerOneUsername, playerTwoUsername))
        }.run()
    }

    fun bidsCompletedUpdate(bidWinnerId: String, biddingPower: Int, username: String) {
        messagingTemplate.convertAndSend("/topic/public/$username",
                BidsReadyMessage(bidWinnerId, biddingPower))
    }

    fun moveUpdate(cells: String, vararg usernames: String) {
        for (username in usernames) {
            messagingTemplate.convertAndSend("/topic/public/$username", MoveUpdateMessage(cells))
        }
    }

    fun victoryUpdate(winnerId: String, vararg usernames: String) {
        for (username in usernames) {
            messagingTemplate.convertAndSend("/topic/public/$username", WinnerUpdateMessage(winnerId))
        }
    }
}