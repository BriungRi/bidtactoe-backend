package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.exceptions.BadLoginException
import li.brianv.bidtactoe.gameservice.game.GameManager
import li.brianv.bidtactoe.gameservice.model.user.User
import li.brianv.bidtactoe.gameservice.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:3000", "http://tactoe.bid"])
@RestController
class GameRestController(val gameManager: GameManager, val userRepository: UserRepository) {

    val logger: Logger = LoggerFactory.getLogger(GameRestController::class.java.simpleName)

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/join_game"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun joinGame(username: String, deviceType: String, deviceToken: String) {
        logger.info("Join Game: username: $username, deviceType: $deviceType, deviceToken: $deviceToken")
        gameManager.joinGame(username, deviceType, deviceToken)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/add_ai"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun addAI() {
        logger.info("Add AI")
        gameManager.addAI()
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/leave_queue"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    fun leaveQueue(username: String) {
        logger.info("Leave Queue: username: $username")
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
        logger.info("Make Move: gameIndex $gameIndex, cells: $cells")
        gameManager.makeMove(gameIndex, cells)
    }

    @RequestMapping(method = [(RequestMethod.POST)],
            value = ["/login"],
            consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
    @ResponseBody
    fun logIn(email: String, password: String): User {
        logger.info("Log In: email $email, password: $password")
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
        logger.info("Sign Up: username: $username, email $email, password: $password")
        userRepository.createUser(username, email, password)
    }

}

fun String.convertStringToCellsArray(): IntArray {
    return this.toLowerCase().toCharArray().map {
        when (it) {
            'o' -> 1
            'x' -> 2
            else -> 0
        }
    }.toIntArray()
}