package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.exceptions.BadLoginException
import li.brianv.bidtactoe.gameservice.game.GameManager
import li.brianv.bidtactoe.gameservice.model.User
import li.brianv.bidtactoe.gameservice.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(val gameManager: GameManager, val userRepository: UserRepository) {

    val logger: Logger = LoggerFactory.getLogger(GameController::class.java.simpleName)

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/join_game",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun joinGame(playerId: String, deviceId: String) {
        logger.info("Join Game: playerId: $playerId, deviceId: $deviceId")
        gameManager.joinGame(playerId, deviceId)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/leave_queue",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun leaveQueue(playerId: String) {
        logger.info("Leave Queue: playerId: $playerId")
        gameManager.leaveQueue(playerId)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/bid",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun bid(playerId: String, gameIndex: Int, bidAmt: Int) {
        logger.info("Bid: playerId: $playerId, gameIndex: $gameIndex, bidAmt: $bidAmt")
        gameManager.bid(playerId, gameIndex, bidAmt)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/make_move",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun makeMove(gameIndex: Int, cells: String) {
        logger.info("Make Move: gameIndex $gameIndex, cells: $cells")
        for (cell in cells.convertStringToCellsArray()) {
            logger.info("cell: $cell")
        }
        gameManager.makeMove(gameIndex, cells)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/login",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    @ResponseBody
    fun logIn(email: String, password: String): User {
        logger.info("Log In: email $email, password: $password")
        val user = userRepository.authenticate(email, password)
        if (user != null)
            return user
        else
            throw BadLoginException()
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST),
            value = "/signup",
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
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