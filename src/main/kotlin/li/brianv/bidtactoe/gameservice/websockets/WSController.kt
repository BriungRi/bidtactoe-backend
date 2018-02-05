package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.GameReadyMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class WSController {
    val logger: Logger = LoggerFactory.getLogger(WSController::class.java.simpleName)

    val random = Math.random()

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    @Throws(Exception::class)
    fun greeting(message: HelloMessage): Greeting {
        logger.info("greeting()")
        return Greeting("Hello, " + message.name + "!")
    }

    @MessageMapping("/join_game")
    @SendTo("/topic/game_ready_update")
    @Throws(Exception::class)
    fun gameReadyUpdate(gameIndex: String, playerOneId: String, playerTwoId: String): GameReadyMessage {
        logger.info("gameReadyUpdate() gameIndex: $gameIndex, playerOneId: $playerOneId, playerTwoId: $playerTwoId")
        return GameReadyMessage(gameIndex, playerOneId, playerTwoId)
    }
}
