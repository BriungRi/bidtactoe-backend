package li.brianv.bidtactoe.gameservice.game.player

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.brianv.bidtactoe.gameservice.game.player.ai.QLearningPlayer
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import org.junit.Test

class QLearningPlayerTest {

    private val aiRepository = mock<AIRepository> {
        on { getBestBidAmtByQValue(any(), any()) } doReturn Pair(0, 0.0)
        on { getBestOpenPositionByQValue(any(), any(), any(), any()) } doReturn Pair(4, 0.0)
    }
    private val qLearningPlayer = QLearningPlayer(aiRepository, mock(), mock(), true)

    /* Correctness tests
       it is assumed that !isPlayerOne
     */
    @Test
    fun getBidAmt_opponentWillWin() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150, 200)
        val cases = arrayOf("OO       ", " OO      ", " O  O    ", "O   O    ")
        for (biddingPower in biddingPowers) {
            for (case in cases) {
                val bidAmt = qLearningPlayer.getBidAmt(biddingPower, case)
                assert(bidAmt == Math.min((200 - biddingPower) + 1, biddingPower))
            }
        }
    }

    @Test
    fun getBidAmt_playerWillWin_inOneMove() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150)
        val cases = arrayOf("XX       ", " XX      ", " X  X    ", "X   X    ", "XOO X    ")
        for (biddingPower in biddingPowers) {
            for (case in cases) {
                val bidAmt = qLearningPlayer.getBidAmt(biddingPower, case)
                assert(bidAmt == biddingPower)
            }
        }
    }

    @Test
    fun getBidAmt_playerWillWin_inTwoMoves() {
        val biddingPowers = intArrayOf(151, 160, 170, 180, 200)
        val cases = arrayOf("X        ", "  X      ", " X       ", "        X", "XOO      ", "OO  X    ")
        for (biddingPower in biddingPowers) {
            for (case in cases) {
                val bidAmt = qLearningPlayer.getBidAmt(biddingPower, case)
                assert(bidAmt == 200 - biddingPower + 1)
            }
        }
    }

    @Test
    fun getMoveIndex_opponentWillWin() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150, 200)
        val cases = arrayOf("OO       ", "     O  O", "    O O  ")
        for (biddingPower in biddingPowers) {
            for (case in cases) {
                val moveIndex = qLearningPlayer.getMoveIndex(biddingPower, case)
                assert(moveIndex == 2)
            }
        }
    }

    @Test
    fun getMoveIndex_playerWillWin() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150, 200)
        val cases = arrayOf("XX       ", "     X  X", "    X X  ")
        for (biddingPower in biddingPowers) {
            for (case in cases) {
                val moveIndex = qLearningPlayer.getMoveIndex(biddingPower, case)
                assert(moveIndex == 2)
            }
        }
    }

    @Test
    fun getMoveIndex_playerWillWin_opponentCanWin() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150, 200)
        val cases = arrayOf("XX    OO ", "O  O X  X", "O  OX X  ")
        for (biddingPower in biddingPowers) {
        for (case in cases) {
            val moveIndex = qLearningPlayer.getMoveIndex(biddingPower, case)
            assert(moveIndex == 2)
        }
        }
    }

    @Test
    fun getMoveIndex_playerHasOneDown() {
        val biddingPowers = intArrayOf(0, 50, 100, 125, 150, 200)
        val cases = arrayOf("X        ", "   X     ", "      X  ")
        for (biddingPower in biddingPowers) {
            val expected = arrayOf(1, 4, 7)
            for (case in cases.zip(expected)) {
                val moveIndex = qLearningPlayer.getMoveIndex(biddingPower, case.first)
                assert(moveIndex == case.second)
            }
        }
    }

    /* Validity tests */

    @Test
    fun getBidAmt() {
        for (i in 0..1000) {
            val bidAmt = qLearningPlayer.getBidAmt(100, "         ")
            assert(bidAmt in 0..100)
        }
    }

    @Test
    fun getMoveIndex_noMoves() {
        for (i in 0..100) {
            val moveIndex = qLearningPlayer.getMoveIndex(5, "         ")
            assert(moveIndex in 0..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveX() {
        for (i in 0..100) {
            val moveIndex = qLearningPlayer.getMoveIndex(5, "X        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveO() {
        for (i in 0..100) {
            val moveIndex = qLearningPlayer.getMoveIndex(5, "O        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftXs() {
        for (i in 0..100) {
            val moveIndex = qLearningPlayer.getMoveIndex(5, "XXXX XXXX")
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftOs() {
        for (i in 0..100) {
            val moveIndex = qLearningPlayer.getMoveIndex(5, "OOOO OOOO")
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_corners() {
        val biddingPower = 150
        val cases = arrayOf(
                "   " +
                " O " +
                "   ")
        val expected = arrayOf(0)
        for (case in cases.zip(expected)) {
            val moveIndex = qLearningPlayer.getMoveIndex(biddingPower, case.first)
            assert(moveIndex == case.second)
        }
    }

}