package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent

class AndroidPlayer(override val username: String,
                    private val gameFCMComponent: GameFCMComponent,
                    private val deviceToken: String) : Player() {

    override fun onGameReady(gameIndex: Int, playerOneUsername: String, playerTwoUsername: String) =
            gameFCMComponent.gameReadyUpdate(gameIndex.toString(), playerOneUsername, playerTwoUsername, deviceToken)

    override fun onBidsCompleted(bidWinnerUsername: String, newBiddingPower: Int) =
            gameFCMComponent.bidsCompletedUpdate(bidWinnerUsername, newBiddingPower.toString(), deviceToken)

    override fun onMoveCompleted(newCells: String) =
            gameFCMComponent.moveUpdate(newCells, deviceToken)

    override fun onGameOver(winnerUsername: String) =
            gameFCMComponent.victoryUpdate(winnerUsername, deviceToken)
}
