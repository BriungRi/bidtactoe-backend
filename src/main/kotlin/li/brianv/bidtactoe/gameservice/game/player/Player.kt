package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.exceptions.InsufficientBidPowerException
import li.brianv.bidtactoe.gameservice.game.NO_WINNER_USERNAME

abstract class Player {

    abstract val username: String

    private var biddingPower = 100
    var bidAmt = 0
        private set

    abstract fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String)

    protected abstract fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int)

    abstract fun onMoveCompleted(newCells: String)

    abstract fun onGameOver(winnerUsername: String)

    fun makeBid(bidAmt: Int) {
        if (bidAmt <= biddingPower) {
            this.bidAmt = bidAmt
        } else {
            throw InsufficientBidPowerException()
        }
    }

    fun winnerBidUpdate() {
        biddingPower -= bidAmt
        onBidsCompleted(username, biddingPower)
    }

    fun tieBidUpdate() {
        onBidsCompleted(NO_WINNER_USERNAME, biddingPower)
    }

    fun loserBidUpdate(opponentUsername: String, opponentBid: Int) {
        biddingPower += opponentBid
        onBidsCompleted(opponentUsername, biddingPower)
    }
}