package li.brianv.bidtactoe.gameservice.game.player.ai

import li.brianv.bidtactoe.gameservice.game.EMPTY_SPACE
import java.util.*
import kotlin.collections.ArrayList

open class NormalDistPlayer : AIPlayer() {
    override fun getAICode(): String {
        return "NDP"
    }

    private val random = Random()

    /**
     * Let the 3rd standard deviation give us the limit of the bidding range. If it ever exceeds, we can clamp the values at the min and max
     */
    override fun getBidAmt(biddingPower: Int, cells: String): Int {
        val mean = 25
        val std = 10

        return Math.max(Math.min(((random.nextGaussian() * std) + mean).toInt(), biddingPower), 0)
    }

    /**
     * Finds random spot to place the piece
     */
    override fun getMoveIndex(biddingPower: Int, cells: String): Int {
        val openPositions = ArrayList<Int>()
        for (i in 0 until cells.length) {
            if (cells[i] == EMPTY_SPACE)
                openPositions.add(i)
        }
        return openPositions[(Math.random() * openPositions.size).toInt()]
    }
}