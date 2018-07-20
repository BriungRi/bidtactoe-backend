package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.exceptions.BadLoginException
import li.brianv.bidtactoe.gameservice.game.GameManager
import li.brianv.bidtactoe.gameservice.model.GameCode
import li.brianv.bidtactoe.gameservice.model.Number
import li.brianv.bidtactoe.gameservice.model.user.User
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import li.brianv.bidtactoe.gameservice.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000", "http://tactoe.bid", "http://localhost:3001"])
@RestController
class GameRestController(val gameManager: GameManager, val userRepository: UserRepository, val aiRepository: AIRepository) {

    val logger: Logger = LoggerFactory.getLogger(GameRestController::class.java.simpleName)

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/create_game"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    @ResponseBody
    fun createGame(username: String, deviceType: String, deviceToken: String): GameCode {
        return gameManager.createGame(username, deviceType, deviceToken)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/join_game"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    @ResponseBody
    fun joinGame(username: String, deviceType: String, deviceToken: String, gameCode: String) {
        gameManager.joinGame(username, deviceType, deviceToken, gameCode)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/join_random_game"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun joinRandomGame(username: String, deviceType: String, deviceToken: String) {
        gameManager.joinRandomGame(username, deviceType, deviceToken)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/add_ai"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun addAI() {
        gameManager.addAI()
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/leave_queue"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun leaveQueue(username: String) {
        gameManager.leaveQueue(username)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/bid"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun bid(username: String, gameIndex: Int, bidAmt: Int) {
        gameManager.bid(username, gameIndex, bidAmt)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/make_move"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun makeMove(gameIndex: Int, cells: String) {
        gameManager.makeMove(gameIndex, cells)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/login"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    @ResponseBody
    fun logIn(email: String, password: String): User {
        val user = userRepository.authenticate(email, password)
        if (user != null)
            return user
        else
            throw BadLoginException()
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/signup"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    @ResponseBody
    fun signUp(username: String, email: String, password: String) {
        userRepository.createUser(username, email, password)
    }

    @GetMapping(value = ["/num_ai_wins"])
    fun getNumAIWins(): Number {
        return Number(aiRepository.getNumEvalWins())
    }

    @GetMapping(value = ["/num_ai_ties"])
    fun getNumAITies(): Number {
        return Number(aiRepository.getNumEvalTies())
    }

    @RequestMapping(value = ["/num_ai_losses"])
    fun getNumAIGames(): Number {
        return Number(aiRepository.getNumEvalLosses())
    }
}
