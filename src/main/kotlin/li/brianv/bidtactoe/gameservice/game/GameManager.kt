package li.brianv.bidtactoe.gameservice.game

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import java.util.*

class GameManager(private val playerQueue: Queue<Player>,
                  private val gameArray: ArrayList<Game>,
                  private val moveMaker: MoveMaker,
                  private val gameFCMComponent: GameFCMComponent,
                  private val gameWSComponent: GameWSComponent) {

    private val NO_WINNER = "no_winner"

    fun joinGame(username: String, deviceId: String) { // TODO: Synchronize this method?
        playerQueue.add(Player(username, deviceId, -1, 100))
        if (playerQueue.size >= 2) {
            val playerOne = playerQueue.poll()
            val playerTwo = playerQueue.poll()
            val game = Game("         ", playerOne, playerTwo)
            gameArray.add(game) // TODO: Game removal
            //TODO: Create games in null spaces
            val gameIndex = gameArray.size - 1
            gameFCMComponent.gameReadyUpdate(gameIndex.toString(),
                    playerOne.username,
                    *game.getPlayerDeviceTokens())
            gameWSComponent.gameReadyUpdate(gameIndex,
                    playerOne.username,
                    playerTwo.username)
        }
    }

    fun leaveQueue(username: String) {
        playerQueue
                .filter { it.username == username }
                .forEach { playerQueue.remove(it) }
    }

    fun bid(username: String, gameIndex: Int, bidAmt: Int) {
        val game = gameArray[gameIndex]
        moveMaker.bid(game, username, bidAmt)
        if (game.playerOne.hasBid() && game.playerTwo.hasBid()) {
            val bidWinnerId = getBidWinner(game.playerOne, game.playerTwo)
            if (bidWinnerId != NO_WINNER)
                applyBids(game.playerOne, game.playerTwo, bidWinnerId)
            gameFCMComponent.bidsCompletedUpdate(bidWinnerId, // TODO: Verbose method signature
                    game.playerOne.biddingPower.toString(),
                    game.playerOne.deviceToken)
            gameFCMComponent.bidsCompletedUpdate(bidWinnerId,
                    game.playerTwo.biddingPower.toString(),
                    game.playerTwo.deviceToken)
            gameWSComponent.bidsCompletedUpdate(bidWinnerId, // TODO: One component to handle push notifications
                    game.playerOne.biddingPower,
                    game.playerOne.username)
            gameWSComponent.bidsCompletedUpdate(bidWinnerId,
                    game.playerTwo.biddingPower,
                    game.playerTwo.username)
            game.playerOne.resetBid()
            game.playerTwo.resetBid()
        }
    }

    private fun getBidWinner(playerOne: Player, playerTwo: Player): String {
        if (playerOne.currentBid != playerTwo.currentBid) {
            return if (playerOne.currentBid > playerTwo.currentBid)
                playerOne.username
            else
                playerTwo.username
        }
        return NO_WINNER
    }

    private fun applyBids(playerOne: Player, playerTwo: Player, winnerId: String) {
        if (playerOne.username == winnerId) {
            playerOne.applyBid()
            playerTwo.gainBiddingPower(playerOne.currentBid)
        } else {
            playerTwo.applyBid()
            playerOne.gainBiddingPower(playerTwo.currentBid)
        }
    }

    fun makeMove(gameIndex: Int, cells: String) {
        val game = gameArray[gameIndex]
        moveMaker.makeMove(game, cells)
        val gameWinner = game.getWinner()
        gameFCMComponent.moveUpdate(cells, *game.getPlayerDeviceTokens())
        gameWSComponent.moveUpdate(cells, *game.getPlayerUsernames())
        if (gameWinner != null) {
            gameFCMComponent.victoryUpdate(gameWinner.username, *game.getPlayerDeviceTokens())
            gameWSComponent.victoryUpdate(gameWinner.username, *game.getPlayerUsernames())
        }
    }
}

fun IntArray.convertCellsToString(): String {
    return this.map { i ->
        when (i) {
            1 -> 'o'
            2 -> 'x'
            else -> ' '
        }
    }.joinToString("")
}