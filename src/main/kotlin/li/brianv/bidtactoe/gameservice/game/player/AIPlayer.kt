package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.game.*
import java.util.*

abstract class AIPlayer(private val gameManager: GameManager) : Player() {

    override val username = ("Guest-" + UUID.randomUUID().toString()).substring(0..13)
    private var gameIndex = -1
    private var isPlayerOne = false
    private var biddingPower = 100
    private var cells = "         "

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) {
        this.gameIndex = gameIndex
        this.isPlayerOne = playerOneUsername == username

        gameManager.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) {
        this.biddingPower = newBiddingPower
        if (username == bidWinnerUsername) {
            val moveIndex = getMoveIndex(biddingPower, cells, isPlayerOne)
            val newCells = StringBuilder(cells)
            newCells[moveIndex] = if (isPlayerOne) PLAYER_ONE_PIECE else PLAYER_TWO_PIECE
            gameManager.makeMove(gameIndex, newCells.toString())
        } else if (username == NO_WINNER_USERNAME) {
            gameManager.bid(username, gameIndex, getBidAmt(biddingPower, cells))
        }
    }

    override fun onMoveCompleted(newCells: String) {
        cells = newCells
        gameManager.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    override fun onGameOver(winnerUsername: String) {}

    abstract fun getBidAmt(biddingPower: Int, cells: String): Int

    abstract fun getMoveIndex(biddingPower: Int, cells: String, isPlayerOne: Boolean): Int
}