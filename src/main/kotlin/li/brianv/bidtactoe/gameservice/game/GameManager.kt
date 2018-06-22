package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.game.player.*
import li.brianv.bidtactoe.gameservice.model.DeviceType
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

val logger: Logger = LoggerFactory.getLogger(GameManager::class.java.simpleName)

class GameManager(private val playerQueue: Queue<Player>,
                  private val gameArray: ArrayList<Game>,
                  private val gameFCMComponent: GameFCMComponent,
                  private val gameWSComponent: GameWSComponent,
                  private val aiRepository: AIRepository) {

    var numPlayers = 0

    fun joinGame(username: String, deviceType: String, deviceToken: String) { // TODO: Synchronize this method?
        when (deviceType) {
            DeviceType.ANDROID.stringName -> playerQueue.add(AndroidPlayer(username, gameFCMComponent, deviceToken))
            DeviceType.WEB.stringName -> playerQueue.add(WebPlayer(username, gameWSComponent))
        }
        checkIfGameCanBeCreated()
    }

    fun addAI() {
        playerQueue.add(
                if (numPlayers % 2 == 0)
                    QLearningPlayer(aiRepository, ArrayList(), ArrayList())
                else
                    NormalDistPlayer())
        checkIfGameCanBeCreated()

        if (numPlayers % 1200 == 0) // Output every 10 minutes. 60 games a minute, 600 games every 10 minutes, 1200 players every 10 minutes
            outputAIPerformance()
    }

    private fun checkIfGameCanBeCreated() {
        numPlayers++
        if (playerQueue.size >= 2) {
            createNewGame()
        }
    }

    private fun createNewGame() {
        thread {
            val game = Game(playerQueue.poll(), playerQueue.poll())
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
        logger.info("Total games,Total wins,Win rate")
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
