package li.brianv.bidtactoe.gameservice.game.player.ai

import li.brianv.bidtactoe.gameservice.game.EMPTY_SPACE
import li.brianv.bidtactoe.gameservice.game.NO_WINNER_USERNAME
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import kotlin.math.roundToInt

private const val BID_KEY_PREFIX = "q:bid"
private const val MOVE_KEY_PREFIX = "q:move"

private const val LEARNING_RATE = 0.1
private const val DISCOUNT_FACTOR = 0.1
private const val PROBABILITY_EXPLORE = 0.05
private const val REWARD = 1.0
private const val TIE_REWARD = 0.0

class QLearningPlayer(private val aiRepository: AIRepository,
                      private val bidActionKeys: MutableList<String>,
                      private val moveActionKeys: MutableList<String>,
                      private val training: Boolean) : AIPlayer() {
    override fun getAICode(): String {
        return "QLP"
    }

    override fun getBidAmt(biddingPower: Int, cells: String): Int {
        getWinningBid(biddingPower, cells)?.let { return it }
        getBlockingBid(biddingPower, cells)?.let { return it }

        val bestBidAmt = aiRepository.getBestBidAmtByQValue(biddingPower, cells).first
        return if (shouldExplore()) {
            val randomBidAmt = (Math.random() * (biddingPower)).roundToInt()
            bidActionKeys.add("$BID_KEY_PREFIX:$biddingPower:$cells:$randomBidAmt")
            randomBidAmt
        } else {
            bidActionKeys.add("$BID_KEY_PREFIX:$biddingPower:$cells:$bestBidAmt")
            bestBidAmt
        }
    }

    override fun getMoveIndex(biddingPower: Int, cells: String): Int {
        getWinningMoveIndex(cells)?.let { return it }
        getBlockingMoveIndex(cells)?.let { return it }
        getConsecutiveMoveIndex(cells)?.let { return it }
        getMiddleIndex(cells)?.let { return it }
        getCornerIndex(cells)?.let { return it }

        val openPositions = getOpenPositions(cells)
        val bestOpenPosition = aiRepository.getBestOpenPositionByQValue(biddingPower, cells, openPositions, isPlayerOne).first
        return if (shouldExplore()) {
            val randomOpenPosition = openPositions[(Math.random() * openPositions.size).toInt()]
            moveActionKeys.add("$MOVE_KEY_PREFIX:$biddingPower:$cells:$randomOpenPosition")
            randomOpenPosition
        } else {
            moveActionKeys.add("$MOVE_KEY_PREFIX:$biddingPower:$cells:$bestOpenPosition")
            bestOpenPosition
        }
    }

    private fun shouldExplore(): Boolean {
        return training && Math.random() < PROBABILITY_EXPLORE
    }

    private fun getOpenPositions(cells: String): List<Int> {
        val openPositions = ArrayList<Int>()
        for (i in 0 until cells.length) {
            if (cells[i] == EMPTY_SPACE)
                openPositions.add(i)
        }
        return openPositions
    }

    override fun onGameOver(winnerUsername: String) {
        val didWin = this.username == winnerUsername
        val didTie = winnerUsername == NO_WINNER_USERNAME
        if (training) {
            aiRepository.incrNumGames()
            updateBidQValues(didWin, didTie)
            updateMoveQValues(didWin, didTie)
            if (didWin)
                aiRepository.incrNumWins()
        } else {
            when {
                didWin -> aiRepository.incrNumEvalWins()
                didTie -> aiRepository.incrNumEvalTies()
                else -> aiRepository.incrNumEvalLosses()
            }
        }
    }

    private fun updateBidQValues(didWin: Boolean, didTie: Boolean) {
        val keyToIncrAmt = HashMap<String, Double>()
        for (i in 0 until bidActionKeys.size - 1) {
            val action = bidActionKeys[i]
            val nextAction = bidActionKeys[i + 1]
            val reward = if (didWin) REWARD else if (didTie) TIE_REWARD else -REWARD
            val qValue = aiRepository.getQValue(action)
            val qValueIncrAmt = LEARNING_RATE * (reward + (DISCOUNT_FACTOR * getMaxBidQValue(nextAction) - qValue))
            keyToIncrAmt[action] = qValueIncrAmt
        }
        aiRepository.incrQValues(keyToIncrAmt)
    }

    private fun getMaxBidQValue(key: String): Double {
        val keyComponents = key.split(":")
        val biddingPower = keyComponents[2].toInt()
        val cells = keyComponents[3]
        return aiRepository.getBestBidAmtByQValue(biddingPower, cells).second
    }

    private fun updateMoveQValues(didWin: Boolean, didTie: Boolean) {
        val keyToIncrAmt = HashMap<String, Double>()
        for (i in 0 until moveActionKeys.size - 1) { // TODO: Need to use onMovesCompleted() to get all data. Right now, forgoing last data point
            val action = moveActionKeys[i]
            val nextAction = moveActionKeys[i + 1]
            val reward = if (didWin) REWARD else if (didTie) TIE_REWARD else -REWARD
            val qValue = aiRepository.getQValue(action)
            val qValueIncrAmt = LEARNING_RATE * (reward + (DISCOUNT_FACTOR * getMaxMoveQValue(nextAction) - qValue))
            keyToIncrAmt[action] = qValueIncrAmt
        }
        aiRepository.incrQValues(keyToIncrAmt)
    }

    private fun getMaxMoveQValue(key: String): Double {
        val keyComponents = key.split(":")
        val biddingPower = keyComponents[2].toInt()
        val cells = keyComponents[3]

        val openPositions = getOpenPositions(cells)
        return aiRepository.getBestOpenPositionByQValue(biddingPower, cells, openPositions, isPlayerOne).second
    }

}