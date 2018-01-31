package li.brianv.bidtactoe.gameservice.game

import org.springframework.stereotype.Component

@Component
class MoveMaker {
    fun bid(game: Game, playerId: String, bidAmt: Int) {
        val player = getPlayer(game, playerId)
        if (bidAmt in 0..player.biddingPower)
            player.currentBid = bidAmt
    }

    private fun getPlayer(game: Game, playerId: String): Player {
        if (game.playerOne.playerId == playerId)
            return game.playerOne
        else
            return game.playerTwo
    }

    fun makeMove(game: Game, newCells: String) {
        game.cells = newCells
    }

}