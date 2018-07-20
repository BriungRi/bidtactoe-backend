package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.game.player.AndroidPlayer
import li.brianv.bidtactoe.gameservice.game.player.Player
import li.brianv.bidtactoe.gameservice.game.player.WebPlayer
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.*

class GameManagerTest {

    private val playerQueue = LinkedList<Player>()
    private val gameArray = ArrayList<Game>()
    private val matchQueueMap = HashMap<String, Player>()
    private val gameFCMComponent = mock(GameFCMComponent::class.java)
    private val gameWSComponent = mock(GameWSComponent::class.java)
    private val aiRepository = mock(AIRepository::class.java)
    private val gameManager = GameManager(playerQueue, gameArray, matchQueueMap, gameFCMComponent, gameWSComponent, aiRepository)

    @Test
    fun joinGame_oneWebJoin() {
        gameManager.joinRandomGame("", "web", "")
        Thread.sleep(500)
        assert(gameArray.size == 1)
    }

    @Test
    fun joinGame_oneAndroidJoin() {
        gameManager.joinRandomGame("", "android", "")
        Thread.sleep(500)
        assert(gameArray.size == 1)
    }

    @Test
    fun joinGame_twoJoin() {
        gameManager.joinRandomGame("", "android", "")
        gameManager.joinRandomGame("", "web", "")
        Thread.sleep(500)
        assert(gameArray.size == 2)
    }

    @Test
    fun leaveQueue() {
        val fakeUsername = "fake username"
        gameManager.joinRandomGame(fakeUsername, "web", "")
        gameManager.leaveQueue(fakeUsername)
        assert(playerQueue.isEmpty())
    }

    @Test
    fun bid() {
    }

    @Test
    fun makeMove() {
    }
}