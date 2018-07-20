package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.exceptions.BadDeviceTypeException
import li.brianv.bidtactoe.gameservice.exceptions.BadGameCodeException
import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.game.player.AndroidPlayer
import li.brianv.bidtactoe.gameservice.game.player.Player
import li.brianv.bidtactoe.gameservice.game.player.WebPlayer
import li.brianv.bidtactoe.gameservice.game.player.ai.QLearningPlayer
import li.brianv.bidtactoe.gameservice.game.player.ai.SmartNormalDistPlayer
import li.brianv.bidtactoe.gameservice.model.DeviceType
import li.brianv.bidtactoe.gameservice.model.GameCode
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

val logger: Logger = LoggerFactory.getLogger(GameManager::class.java.simpleName)

class GameManager(private val playerQueue: Queue<Player>,
                  private val gameArray: ArrayList<Game>,
                  private val matchQueueMap: MutableMap<String, Player>,
                  private val gameFCMComponent: GameFCMComponent,
                  private val gameWSComponent: GameWSComponent,
                  private val aiRepository: AIRepository) {

    private var numAIGames = 0
    private var gameIndex = BigInteger.ONE

    @Synchronized
    fun createGame(username: String, deviceType: String, deviceToken: String): GameCode {
        val gameCode = gameIndex.toString()
        when (deviceType) {
            DeviceType.ANDROID.stringName ->
                matchQueueMap[gameCode] = AndroidPlayer(username, gameFCMComponent, deviceToken)
            DeviceType.WEB.stringName ->
                matchQueueMap[gameCode] = WebPlayer(username, gameWSComponent)
        }
        gameIndex = gameIndex.add(BigInteger.ONE)
        return GameCode(gameCode)
    }

    @Synchronized
    fun joinGame(username: String, deviceType: String, deviceToken: String, gameCode: String) {
        if (matchQueueMap.containsKey(gameCode)) {
            matchQueueMap[gameCode]?.let { otherPlayer ->
                val player = when (deviceType) {
                    DeviceType.ANDROID.stringName ->
                        AndroidPlayer(username, gameFCMComponent, deviceToken)
                    DeviceType.WEB.stringName ->
                        WebPlayer(username, gameWSComponent)
                    else ->
                        WebPlayer(username, gameWSComponent)
                }
                createNewGame(player, otherPlayer)
            }
        } else
            throw BadGameCodeException()
    }

    @Synchronized
    fun joinRandomGame(username: String, deviceType: String, deviceToken: String) {
        val player = when (deviceType) {
            DeviceType.ANDROID.stringName -> AndroidPlayer(username, gameFCMComponent, deviceToken)
            DeviceType.WEB.stringName -> WebPlayer(username, gameWSComponent)
            else -> throw BadDeviceTypeException()
        }
        createNewGame(player, QLearningPlayer(aiRepository, ArrayList(), ArrayList(), training = false))
    }

    @Synchronized
    fun addAI() {
        createNewGame(QLearningPlayer(aiRepository, ArrayList(), ArrayList(), training = true), SmartNormalDistPlayer())
        numAIGames++
        if (numAIGames % 1200 == 0)
            outputAIPerformance()
    }

//    private fun checkIfGameCanBeCreated() {
//        if (playerQueue.size >= 2) {
//            createNewGame(playerQueue.poll(), playerQueue.poll())
//        }
//    }

    private fun createNewGame(playerOne: Player, playerTwo: Player) {
        thread {
            val game = Game(playerOne, playerTwo)
            val nextGameIndex = getNextGameIndex()
            if (nextGameIndex < gameArray.size)
                gameArray[nextGameIndex] = game
            else
                gameArray.add(game)

            game.sendGameReadyUpdate(nextGameIndex)
        }
    }

    private fun getNextGameIndex(): Int {
        for (i in 0 until gameArray.size) {
            if (gameArray[i].gameIsOver)
                return i
        }
        return gameArray.size
    }

    private fun outputAIPerformance() {
        val numGames = aiRepository.getNumGames().toDouble()
        val numWins = aiRepository.getNumWins().toDouble()
        val winRate = (numWins / numGames) * 100
        logger.info("$numGames,$numWins,$winRate")
    }

    fun leaveQueue(username: String) {
        val iterator = playerQueue.iterator()
        while (iterator.hasNext()) {
            val player = iterator.next()
            if (player.username == username)
                iterator.remove()
        }
    }

    fun bid(username: String, gameIndex: Int, bidAmt: Int) =
            gameArray[gameIndex].bid(username, bidAmt)

    fun makeMove(gameIndex: Int, cells: String) =
            gameArray[gameIndex].move(cells)
}
