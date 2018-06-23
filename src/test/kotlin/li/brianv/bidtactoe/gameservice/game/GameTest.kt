package li.brianv.bidtactoe.gameservice.game

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.brianv.bidtactoe.gameservice.exceptions.IllegalMoveException
import li.brianv.bidtactoe.gameservice.game.player.Player
import org.junit.Test

private const val playerOneUsername = "a"
private const val playerTwoUsername = "b"
class GameTest {

    private val playerOne = mock<Player> {
        on { username } doReturn playerOneUsername
    }
    private val playerTwo = mock<Player> {
        on { username } doReturn playerTwoUsername
    }
    private val game = Game(playerOne, playerTwo)

    @Test
    fun bidStalemate() {
        assert(!game.gameIsOver)
        game.bid(playerOneUsername, 10)
        game.bid(playerTwoUsername, 10)
        assert(!game.gameIsOver)
        game.bid(playerOneUsername, 10)
        game.bid(playerTwoUsername, 10)
        assert(!game.gameIsOver)
        game.bid(playerOneUsername, 10)
        game.bid(playerTwoUsername, 10)
        assert(game.gameIsOver)
    }

    @Test
    fun getGameIsOver_notOver() {
        assert(!game.gameIsOver)
        game.move("    X    ")
        assert(!game.gameIsOver)
        game.move("    XX   ")
        assert(!game.gameIsOver)
    }

    @Test
    fun getGameIsOver_gameOver() {
        game.move("    X    ")
        game.move("    XX   ")
        game.move("   XXX   ")
        assert(game.gameIsOver)
    }

    @Test
    fun getGameIsOver_gameOver_tie() {
        game.move("    X    ")
        game.move("    XX   ")
        game.move("   OXX   ")
        game.move("  OOXX   ")
        game.move(" XOOXX   ")
        game.move("XXOOXX   ")
        game.move("XXOOXXX  ")
        game.move("XXOOXXXO ")
        game.move("XXOOXXXOO")
        assert(game.gameIsOver)
    }

    @Test
    fun move() {
        game.move("    X    ")
    }

    @Test(expected = IllegalMoveException::class)
    fun move_noMove() {
        game.move("         ")
    }

    @Test(expected = IllegalMoveException::class)
    fun move_wrongSize() {
        game.move("        ")
    }

    @Test(expected = IllegalMoveException::class)
    fun move_illegalCharacter() {
        game.move("    z    ")
    }

    @Test(expected = IllegalMoveException::class)
    fun move_illegalNumChanges() {
        game.move("    XX   ")
    }

}