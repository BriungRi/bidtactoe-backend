package li.brianv.bidtactoe.gameservice.game.player

import com.mashape.unirest.http.Unirest
import li.brianv.bidtactoe.gameservice.game.NO_WINNER_USERNAME
import li.brianv.bidtactoe.gameservice.game.PLAYER_ONE_PIECE
import li.brianv.bidtactoe.gameservice.game.PLAYER_TWO_PIECE
import java.util.*
import kotlin.concurrent.thread


abstract class AIPlayer : Player() {

    override val username = ("Guest-" + UUID.randomUUID().toString()).substring(0..13)
    private var gameIndex = -1
    protected var isPlayerOne = false
    private var biddingPower = 100
    private var cells = "         "

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) {
        this.gameIndex = gameIndex
        this.isPlayerOne = playerOneUsername == username

        this.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) {
        this.biddingPower = newBiddingPower
        if (username == bidWinnerUsername) {
            val moveIndex = getMoveIndex(biddingPower, cells)
            val newCells = StringBuilder(cells)
            newCells[moveIndex] = if (isPlayerOne) PLAYER_ONE_PIECE else PLAYER_TWO_PIECE
            this.makeMove(gameIndex, newCells.toString())
        } else if (bidWinnerUsername == NO_WINNER_USERNAME) {
            this.bid(username, gameIndex, getBidAmt(biddingPower, cells))
        }
    }

    override fun onMoveCompleted(newCells: String) {
        cells = newCells
        this.bid(username, gameIndex, getBidAmt(biddingPower, cells))
    }

    private fun bid(username: String, gameIndex: Int, bidAmt: Int) {
        thread {
            Unirest.post("http://localhost:3001/bid")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "7dd78a06-d45d-4010-94d9-9c7b827f7a6d")
                    .body("username=$username&gameIndex=$gameIndex&bidAmt=$bidAmt")
                    .asString()
        }
    }

    private fun makeMove(gameIndex: Int, cells: String) {
        thread {
            Unirest.post("http://localhost:3001/make_move")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "dcb9f446-75b6-406d-9c75-c10b3ca3a0de")
                    .body("gameIndex=$gameIndex&cells=$cells")
                    .asString()
        }
    }

    override fun onGameOver(winnerUsername: String) {
    }

    abstract fun getBidAmt(biddingPower: Int, cells: String): Int

    abstract fun getMoveIndex(biddingPower: Int, cells: String): Int
}