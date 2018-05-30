package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.GameRestController
import li.brianv.bidtactoe.gameservice.game.PLAYER_ONE_PIECE
import li.brianv.bidtactoe.gameservice.game.PLAYER_TWO_PIECE

abstract class AIPlayer(val gameRestController: GameRestController) : Player() {

    override val username = "AI"
    private var gameIndex = -1
    private var isPlayerOne = false
    private var biddingPower = 100
    private var cells = "         "

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) {
        this.gameIndex = gameIndex
        this.isPlayerOne = playerOneUsername == username
        gameRestController.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) {
        this.biddingPower = newBiddingPower
        if (username == bidWinnerUsername) {
            val moveIndex = getMoveIndex(biddingPower, cells, isPlayerOne)
            val newCells = cells.substring(0, moveIndex) +
                    if (isPlayerOne) PLAYER_ONE_PIECE else PLAYER_TWO_PIECE +
                            cells.substring(moveIndex + 1)
            gameRestController.makeMove(gameIndex, newCells)
        }
    }

    override fun onMoveCompleted(newCells: String) {
        cells = newCells
        gameRestController.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    override fun onGameOver(winnerUsername: String) {}

    abstract fun getBidAmt(biddingPower: Int, cells: String): Int

    abstract fun getMoveIndex(biddingPower: Int, cells: String, isPlayerOne: Boolean): Int
}