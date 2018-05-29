package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.exceptions.IllegalMoveException
import li.brianv.bidtactoe.gameservice.exceptions.PlayerGameMismatchException
import li.brianv.bidtactoe.gameservice.game.player.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val NO_WINNER_USERNAME = "no_winner"
const val EMPTY_SPACE = ' '
const val MANDATORY_CELLS_SIZE = 9
val WIN_POSITIONS: Array<Array<Int>> = arrayOf(arrayOf(0, 1, 2),
        arrayOf(3, 4, 5),
        arrayOf(6, 7, 8),
        arrayOf(0, 3, 6),
        arrayOf(1, 4, 7),
        arrayOf(2, 5, 8),
        arrayOf(0, 4, 8),
        arrayOf(2, 4, 6))

const val PLAYER_ONE_PIECE: Char = 'O'
const val PLAYER_TWO_PIECE: Char = 'X'

class Game(private val playerOne: Player,
           private val playerTwo: Player) {

    val logger: Logger = LoggerFactory.getLogger(Game::class.java.simpleName)
    private var cells: String = "         "
    var gameIsOver = false
        private set

    fun sendGameReadyUpdate(gameIndex: Int) {
        playerOne.onGameReady(gameIndex, playerOne.username, playerTwo.username)
        playerTwo.onGameReady(gameIndex, playerOne.username, playerTwo.username)
    }

    fun bid(username: String, bidAmt: Int) {
        when (username) {
            playerOne.username -> {
                handleBid(playerOne, bidAmt)
            }
            playerTwo.username -> {
                handleBid(playerTwo, bidAmt)
            }
            else -> throw PlayerGameMismatchException()
        }
    }

    private fun handleBid(player: Player, bidAmt: Int) {
        player.makeBid(bidAmt)
        if (bothPlayersMadeBids()) {
            when (getBidWinnerUsername()) {
                playerOne.username -> {
                    playerOne.winnerBidUpdate()
                    playerTwo.loserBidUpdate(playerOne.username, playerOne.bidAmt)
                }
                playerTwo.username -> {
                    playerOne.loserBidUpdate(playerTwo.username, playerTwo.bidAmt)
                    playerTwo.winnerBidUpdate()
                }
                else -> {
                    playerOne.tieBidUpdate()
                    playerTwo.tieBidUpdate()
                }
            }
            playerOne.resetBidParameters()
            playerTwo.resetBidParameters()
        }
    }

    private fun bothPlayersMadeBids(): Boolean {
        return playerOne.madeBid && playerTwo.madeBid
    }

    private fun getBidWinnerUsername(): String {
        return when {
            playerOne.bidAmt > playerTwo.bidAmt -> playerOne.username
            playerOne.bidAmt < playerTwo.bidAmt -> playerTwo.username
            else -> NO_WINNER_USERNAME
        }
    }

    fun move(newCells: String) {
        if (validMove(cells, newCells)) {
            this.cells = newCells
            playerOne.onMoveCompleted(cells)
            playerTwo.onMoveCompleted(cells)
            if (gameOver()) {
                val gameWinnerUsername = getGameWinnerUsername()
                playerOne.onGameOver(gameWinnerUsername)
                playerTwo.onGameOver(gameWinnerUsername)
                gameIsOver = true
            }
        } else {
            throw IllegalMoveException()
        }
    }

    private fun validMove(oldCells: String, newCells: String): Boolean {
        return newCellsCorrectSize(newCells) &&
                legalCharacters(newCells) &&
                cellsModified(oldCells, newCells) &&
                oldMovesUnmodified(oldCells, newCells) &&
                legalNumChanges(oldCells, newCells)
    }

    private fun newCellsCorrectSize(newCells: String): Boolean {
        return newCells.length == MANDATORY_CELLS_SIZE
    }

    private fun legalCharacters(newCells: String): Boolean {
        return newCells.replace(" ", "")
                .toCharArray()
                .filterNot { it == PLAYER_ONE_PIECE }
                .filterNot { it == PLAYER_TWO_PIECE }
                .isEmpty()
    }

    private fun cellsModified(oldCells: String, newCells: String): Boolean {
        return oldCells != newCells
    }

    private fun oldMovesUnmodified(oldCells: String, newCells: String): Boolean {
        for (i in 0 until oldCells.length) {
            if (oldCells[i] != EMPTY_SPACE && oldCells[i] != newCells[i])
                return false
        }
        return true
    }

    private fun legalNumChanges(oldCells: String, newCells: String): Boolean {
        var numChanges = 0
        for (i in 0 until oldCells.length) {
            if (oldCells[i] != newCells[i])
                numChanges++
            if (numChanges > 1)
                return false
        }
        return true
    }

    private fun gameOver(): Boolean {
        for (winPosition in WIN_POSITIONS) {
            if (cellFilled(cells[winPosition[0]]) &&
                    cells[winPosition[0]] == cells[winPosition[1]] &&
                    cells[winPosition[1]] == cells[winPosition[2]])
                return true
        }
        return gameTied()
    }

    private fun cellFilled(cell: Char): Boolean {
        return cell == PLAYER_ONE_PIECE || cell == PLAYER_TWO_PIECE
    }

    private fun gameTied(): Boolean {
        return !cells.contains(EMPTY_SPACE)
    }

    /**
     * Precondition: The game is already over
     */
    private fun getGameWinnerUsername(): String {
        for (winPosition in WIN_POSITIONS) {
            if (cells[winPosition[0]] == cells[winPosition[1]] && cells[winPosition[1]] == cells[winPosition[2]]) {
                if (PLAYER_ONE_PIECE == cells[winPosition[0]])
                    return playerOne.username
                else if (PLAYER_TWO_PIECE == cells[winPosition[0]])
                    return playerTwo.username
            }
        }
        return NO_WINNER_USERNAME
    }
}