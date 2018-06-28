package li.brianv.bidtactoe.gameservice.game.player.ai

import li.brianv.bidtactoe.gameservice.game.EMPTY_SPACE
import li.brianv.bidtactoe.gameservice.game.PLAYER_ONE_PIECE
import li.brianv.bidtactoe.gameservice.game.PLAYER_TWO_PIECE
import li.brianv.bidtactoe.gameservice.game.WIN_POSITIONS
import java.util.*
import kotlin.collections.ArrayList

class SmartNormalDistPlayer : AIPlayer() {

    private val random = Random()

    /**
     * Let the 3rd standard deviation give us the limit of the bidding range. If it ever exceeds, we can clamp the values at the min and max
     * If the other player's about to win, outbid them to block
     * If player is about to win, bid all
     */
    override fun getBidAmt(biddingPower: Int, cells: String): Int {
        val playerCanWin = playerCanWin(cells)
        val opponentCanWin = opponentCanWin(cells)
        val opponentBiddingPower = 200 - biddingPower

        return when {
            playerCanWin -> biddingPower
            opponentCanWin -> Math.min(opponentBiddingPower + 1, biddingPower)
            else -> randomBid(biddingPower)
        }
    }

    private fun playerCanWin(cells: String): Boolean {
        for (winPosition in WIN_POSITIONS) {
            val sum = winPosition.map { position ->
                when (cells[position]) {
                    PLAYER_ONE_PIECE -> 1
                    PLAYER_TWO_PIECE -> -1
                    else -> {
                        0
                    }
                }
            }.sum()

            if ((isPlayerOne && sum == 2) || (!isPlayerOne && sum == -2)) {
                return true
            }
        }
        return false
    }

    private fun opponentCanWin(cells: String): Boolean {
        for (winPosition in WIN_POSITIONS) {
            val sum = winPosition.map { position ->
                when (cells[position]) {
                    PLAYER_ONE_PIECE -> 1
                    PLAYER_TWO_PIECE -> -1
                    else -> {
                        0
                    }
                }
            }.sum()

            if ((isPlayerOne && sum == -2) || (!isPlayerOne && sum == 2)) {
                return true
            }
        }
        return false
    }

    private fun randomBid(biddingPower: Int): Int {
        val mean = 25
        val std = 10

        return Math.max(Math.min(((random.nextGaussian() * std) + mean).toInt(), biddingPower), 0)
    }

    /**
     * If there's two in a row, pick the third spot
     * Else, if the opponent has two in a row, block them
     * Else, if there's a group of 3 with only one of the player's pieces, pick one of those spots
     * Else, random move
     */
    override fun getMoveIndex(biddingPower: Int, cells: String): Int {
        for (winPosition in WIN_POSITIONS) {
            val sum = winPosition.map { position ->
                when (cells[position]) {
                    PLAYER_ONE_PIECE -> 1
                    PLAYER_TWO_PIECE -> -1
                    else -> {
                        0
                    }
                }
            }.sum()
            if (isPlayerOne && sum == 2 || !isPlayerOne && sum == -2)
                return winPosition.first { position -> cells[position] == EMPTY_SPACE } // Return winning position
        }
        for (winPosition in WIN_POSITIONS) {
            val sum = winPosition.map { position ->
                when (cells[position]) {
                    PLAYER_ONE_PIECE -> 1
                    PLAYER_TWO_PIECE -> -1
                    else -> {
                        0
                    }
                }
            }.sum()
            if (isPlayerOne && sum == -2 || !isPlayerOne && sum == 2)
                return winPosition.first { position -> cells[position] == EMPTY_SPACE } // Return blocking position
        }
        for (winPosition in WIN_POSITIONS) {
            val winPositionCells = winPosition.map { position ->
                cells[position]
            }
            val numSpaces = winPositionCells.map { winPositionCell ->
                when (winPositionCell) {
                    PLAYER_ONE_PIECE -> 0
                    PLAYER_TWO_PIECE -> 0
                    else -> {
                        1
                    }
                }
            }.sum()
            if ((isPlayerOne && numSpaces == 2 && winPositionCells.contains(PLAYER_ONE_PIECE)) ||
                            (!isPlayerOne && numSpaces == 2 && winPositionCells.contains(PLAYER_TWO_PIECE)))
                return winPosition.first { position -> cells[position] == EMPTY_SPACE }
        }
        return if (cells[4] == EMPTY_SPACE)
            4
        else {
            val openPositions = ArrayList<Int>()
            for (i in 0 until cells.length) {
                if (cells[i] == EMPTY_SPACE)
                    openPositions.add(i)
            }
            openPositions[(Math.random() * openPositions.size).toInt()]
        }
    }
}