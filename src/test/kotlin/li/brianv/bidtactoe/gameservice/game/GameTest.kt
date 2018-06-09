package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.exceptions.IllegalMoveException
import li.brianv.bidtactoe.gameservice.game.player.Player
import org.junit.Test
import org.mockito.Mockito.mock

class GameTest {

    private val game = Game(mock(Player::class.java), mock(Player::class.java))

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