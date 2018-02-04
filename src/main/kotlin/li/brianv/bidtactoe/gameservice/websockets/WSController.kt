package li.brianv.bidtactoe.gameservice.websockets

import li.brianv.bidtactoe.gameservice.model.GameReadyMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class WSController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    @Throws(Exception::class)
    fun greeting(message: HelloMessage): Greeting {
        return Greeting("Hello, " + message.name + "!")
    }

    @SendTo("/queue/game_ready_update")
    @Throws(Exception::class)
    fun gameReadyUpdate(gameIndex: String, playerOneId: String, playerTwoId: String): GameReadyMessage {
        return GameReadyMessage(gameIndex, playerOneId, playerTwoId)
    }
}
