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
    private val gameFCMComponent = mock(GameFCMComponent::class.java)
    private val gameWSComponent = mock(GameWSComponent::class.java)
    private val aiRepository = mock(AIRepository::class.java)
    private val gameManager = GameManager(playerQueue, gameArray, gameFCMComponent, gameWSComponent, aiRepository)

    @Test
    fun joinGame_oneWebJoin() {
        gameManager.joinGame("", "web", "")
        assert(playerQueue[0] is WebPlayer)
    }

    @Test
    fun joinGame_oneAndroidJoin() {
        gameManager.joinGame("", "android", "")
        assert(playerQueue[0] is AndroidPlayer)
    }

    @Test
    fun joinGame_twoJoin() {
        gameManager.joinGame("", "android", "")
        gameManager.joinGame("", "web", "")
        Thread.sleep(1000)
        assert(playerQueue.isEmpty())
        assert(!gameArray.isEmpty())
    }

    @Test
    fun leaveQueue() {
        val fakeUsername = "fake username"
        gameManager.joinGame(fakeUsername, "web", "")
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