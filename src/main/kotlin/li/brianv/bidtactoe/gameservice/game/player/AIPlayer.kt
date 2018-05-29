package li.brianv.bidtactoe.gameservice.game.player

abstract class AIPlayer : Player() {

    override val username = "AI"
    private var gameIndex = 0
    private var isPlayerOne = false
    private var biddingPower = 100
    private var cells = "         "

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) {
        this.gameIndex = gameIndex
        this.isPlayerOne = playerOneUsername == username
        getBidAmt(biddingPower, cells)
    }

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) {
        this.biddingPower = newBiddingPower
        if (username == bidWinnerUsername) {
            getMoveIndex(biddingPower, cells, isPlayerOne)
        }
    }

    override fun onMoveCompleted(newCells: String) {
        cells = newCells
        getBidAmt(biddingPower, cells)
    }

    override fun onGameOver(winnerUsername: String) {}

    abstract fun getBidAmt(biddingPower: Int, cells: String): Int

    abstract fun getMoveIndex(biddingPower: Int, cells: String, isPlayerOne: Boolean): Int
}