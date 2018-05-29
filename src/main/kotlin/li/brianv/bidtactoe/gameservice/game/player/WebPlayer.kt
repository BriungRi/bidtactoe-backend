package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent

class WebPlayer(override val username: String,
                private val gameWSComponent: GameWSComponent) : Player() {

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) =
            gameWSComponent.gameReadyUpdate(gameIndex, playerOneUsername, playerTwoUsername, username)

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) =
            gameWSComponent.bidsCompletedUpdate(bidWinnerUsername, newBiddingPower, username)

    override fun onMoveCompleted(newCells: String) =
            gameWSComponent.moveUpdate(newCells, username)

    override fun onGameOver(winnerUsername: String) =
            gameWSComponent.victoryUpdate(winnerUsername, username)
}
