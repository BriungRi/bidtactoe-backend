package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.game.*
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import kotlin.math.roundToInt

private const val BID_KEY_PREFIX = "q:bid"
private const val MOVE_KEY_PREFIX = "q:move"

private const val LEARNING_RATE = 0.1
private const val DISCOUNT_FACTOR = 0.1
private const val PROBABILITY_EXPLORE = 0.4
private const val REWARD = 1.0
private const val TIE_REWARD = 0.1

class QLearningPlayer(private val aiRepository: AIRepository,
                      private val bidActions: MutableList<String>,
                      private val moveActions: MutableList<String>) : AIPlayer() {

    override fun getBidAmt(biddingPower: Int, cells: String): Int {
        var bestBidAmt = Integer.MIN_VALUE
        var bestQValue = Double.NEGATIVE_INFINITY
        for (bidAmt in 0..biddingPower) {
            val qValue = aiRepository.getBidQValue(biddingPower, cells, bidAmt)
            if (qValue > bestQValue) {
                bestBidAmt = bidAmt
                bestQValue = qValue
            }
        }
        return if (shouldExplore()) {
            val action = (Math.random() * (biddingPower)).roundToInt()
            bidActions.add("$BID_KEY_PREFIX:$biddingPower:$cells:$action")
            action
        } else {
            val action = bestBidAmt
            bidActions.add("$BID_KEY_PREFIX:$biddingPower:$cells:$action")
            action
        }
    }

    private fun shouldExplore(): Boolean {
        return Math.random() < PROBABILITY_EXPLORE
    }

    override fun getMoveIndex(biddingPower: Int, cells: String): Int {
        val openPositions = getOpenPositions(cells)
        if (openPositions.isEmpty())
            return -1

        var bestOpenPosition = openPositions[0]
        var bestQValue = Double.NEGATIVE_INFINITY

        for (openPosition in openPositions) {
            val nextCells = getNextCells(cells, openPosition)
            val qValue = aiRepository.getMoveQValue(biddingPower, cells, nextCells)
            if (qValue > bestQValue) {
                bestOpenPosition = openPosition
                bestQValue = qValue
            }
        }
        return if (shouldExplore()) {
            val action = openPositions[(Math.random() * openPositions.size).toInt()]
            moveActions.add("$MOVE_KEY_PREFIX:$biddingPower:$cells:$action")
            action
        } else {
            val action = bestOpenPosition
            val nextCells = getNextCells(cells, action)
            moveActions.add("$MOVE_KEY_PREFIX:$biddingPower:$cells:$nextCells")
            action
        }
    }

    private fun getOpenPositions(cells: String): List<Int> {
        val openPositions = ArrayList<Int>()
        for (i in 0 until cells.length) {
            if (cells[i] == EMPTY_SPACE)
                openPositions.add(i)
        }
        return openPositions
    }

    private fun getPlayerPiece(isPlayerOne: Boolean): Char {
        return if (isPlayerOne) PLAYER_ONE_PIECE else PLAYER_TWO_PIECE
    }

    private fun getNextCells(oldCells: String, openPosition: Int): String {
        return oldCells.substring(0, openPosition) +
                getPlayerPiece(isPlayerOne) +
                oldCells.substring(openPosition + 1)
    }

    override fun onGameOver(winnerUsername: String) {
        super.onGameOver(winnerUsername)
        val didWin = this.username == winnerUsername
        val didTie = winnerUsername == NO_WINNER_USERNAME
        aiRepository.incrNumGames()
        if (isPlayerOne) {
            logger.info("AI " + if (didWin) "won" else if(didTie) "tie" else "lost")
            updateBidQValues(didWin, didTie)
            updateMoveQValues(didWin, didTie)
            if (didWin)
                aiRepository.incrWins()
        }
    }

    private fun updateBidQValues(didWin: Boolean, didTie: Boolean) {
        for (i in 0 until bidActions.size - 1) { // TODO: Need to use onBidsCompleted() to get all data. Right now, forgoing last data point
            val action = bidActions[i]
            val nextAction = bidActions[i + 1]
            val reward = if (didWin) REWARD else if (didTie) TIE_REWARD else -REWARD
            val qValue = aiRepository.getQValue(action)
            val qValueIncrAmt = LEARNING_RATE * (reward + (DISCOUNT_FACTOR * getMaxBidQValue(nextAction) - qValue))
            aiRepository.incrBidQValue(action, qValueIncrAmt)
        }
    }

    // TODO: Duplicate code here
    private fun getMaxBidQValue(key: String): Double {
        val cmpnts = key.split(":")
        val biddingPower = cmpnts[2].toInt()
        val cells = cmpnts[3]
        var bestQValue = Double.NEGATIVE_INFINITY
        for (bidAmt in 0..biddingPower) {
            val qValue = aiRepository.getBidQValue(biddingPower, cells, bidAmt)
            if (qValue > bestQValue) {
                bestQValue = qValue
            }
        }
        return bestQValue
    }

    private fun updateMoveQValues(didWin: Boolean, didTie: Boolean) {
        for (i in 0 until moveActions.size - 1) { // TODO: Need to use onMovesCompleted() to get all data. Right now, forgoing last data point
            val action = moveActions[i]
            val nextAction = moveActions[i + 1]
            val reward = if (didWin) REWARD else if (didTie) TIE_REWARD else -REWARD
            val qValue = aiRepository.getQValue(action)
            val qValueIncrAmt = LEARNING_RATE * (reward + (DISCOUNT_FACTOR * getMaxMoveQValue(nextAction) - qValue))
            aiRepository.incrMoveQValue(action, qValueIncrAmt)
        }
    }

    // TODO: Duplicate code here
    private fun getMaxMoveQValue(key: String): Double {
        val cmpnts = key.split(":")
        val biddingPower = cmpnts[2].toInt()
        val cells = cmpnts[3]

        val openPositions = getOpenPositions(cells)
        if (openPositions.isEmpty())
            return 0.0

        var bestQValue = Double.NEGATIVE_INFINITY

        for (openPosition in openPositions) {
            val nextCells = getNextCells(cells, openPosition)
            val qValue = aiRepository.getMoveQValue(biddingPower, cells, nextCells)
            if (qValue > bestQValue) {
                bestQValue = qValue
            }
        }

        return bestQValue
    }

}