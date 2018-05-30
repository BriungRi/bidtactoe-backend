package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.model.DeviceType
import li.brianv.bidtactoe.gameservice.game.player.AndroidPlayer
import li.brianv.bidtactoe.gameservice.game.player.Player
import li.brianv.bidtactoe.gameservice.game.player.WebPlayer
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import java.util.*

class GameManager(private val playerQueue: Queue<Player>,
                  private val gameArray: ArrayList<Game>,
                  private val gameFCMComponent: GameFCMComponent,
                  private val gameWSComponent: GameWSComponent) {

    fun joinGame(username: String, deviceType: String, deviceToken: String) { // TODO: Synchronize this method?
        when (deviceType) {
            DeviceType.ANDROID.stringName -> playerQueue.add(AndroidPlayer(username, gameFCMComponent, deviceToken))
            DeviceType.WEB.stringName -> playerQueue.add(WebPlayer(username, gameWSComponent))
        }
        if (playerQueue.size >= 2) {
            createNewGame()
        }
    }

    private fun createNewGame() {
        val game = Game(playerQueue.poll(), playerQueue.poll())
        val nextGameIndex = getNextGameIndex()
        game.sendGameReadyUpdate(nextGameIndex)
        if (nextGameIndex < gameArray.size)
            gameArray[nextGameIndex] = game
        else
            gameArray.add(game)
    }

    private fun getNextGameIndex(): Int {
        for (i in 0 until gameArray.size) {
            if (gameArray[i].gameIsOver)
                return i
        }
        return gameArray.size
    }

    fun leaveQueue(username: String) {
        val iterator = playerQueue.iterator()
        while(iterator.hasNext()) {
            val player = iterator.next()
            if(player.username == username)
                iterator.remove()
        }
    }

    fun bid(username: String, gameIndex: Int, bidAmt: Int) = gameArray[gameIndex].bid(username, bidAmt)

    fun makeMove(gameIndex: Int, cells: String) = gameArray[gameIndex].move(cells)
}
