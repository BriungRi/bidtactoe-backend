package li.brianv.bidtactoe.gameservice.game

import org.springframework.stereotype.Component

@Component
class MoveMaker {
    fun bid(game: Game, username: String, bidAmt: Int) {
        val player = getPlayer(game, username)
        if (bidAmt in 0..player.biddingPower)
            player.currentBid = bidAmt
    }

    private fun getPlayer(game: Game, username: String): Player {
        if (game.playerOne.username == username)
            return game.playerOne
        else
            return game.playerTwo
    }

    fun makeMove(game: Game, newCells: String) {
        game.cells = newCells
    }

}