package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.game.player.ai.SmartNormalDistPlayer
import org.junit.Test

class SmartNormalDistPlayerTest {

    private val smartNormalDistPlayer = SmartNormalDistPlayer()

    /* Correctness tests
       it is assumed that !isPlayerOne
     */
    @Test
    fun getBidAmt_opponentWillWin() {
        val biddingPower = 150
        val cases = arrayOf("OO       ", " OO      ", " O  O    ", "O   O    ")
        for (case in cases) {
            val bidAmt = smartNormalDistPlayer.getBidAmt(biddingPower, case)
            assert(bidAmt == (200 - biddingPower) + 1)
        }
    }

    @Test
    fun getBidAmt_opponentWillWin2() {
        val biddingPower = 200
        val cases = arrayOf("OO       ", " OO      ", " O  O    ", "O   O    ")
        for (case in cases) {
            val bidAmt = smartNormalDistPlayer.getBidAmt(biddingPower, case)
            assert(bidAmt == (200 - biddingPower) + 1)
            assert(bidAmt == 1)
        }
    }

    @Test
    fun getBidAmt_playerWillWin() {
        val biddingPower = 150
        val cases = arrayOf("XX       ", " XX      ", " X  X    ", "X   X    ")
        for (case in cases) {
            val bidAmt = smartNormalDistPlayer.getBidAmt(biddingPower, case)
            assert(bidAmt == biddingPower)
        }
    }

    @Test
    fun getMoveIndex_opponentWillWin() {
        val biddingPower = 150
        val cases = arrayOf("OO       ", "     O  O", "    O O  ")
        for (case in cases) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(biddingPower, case)
            assert(moveIndex == 2)
        }
    }

    @Test
    fun getMoveIndex_playerWillWin() {
        val biddingPower = 150
        val cases = arrayOf("XX       ", "     X  X", "    X X  ")
        for (case in cases) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(biddingPower, case)
            assert(moveIndex == 2)
        }
    }

    @Test
    fun getMoveIndex_playerWillWin_opponentCanWin() {
        val biddingPower = 150
        val cases = arrayOf("XX    OO ", "O  O X  X", "O  OX X  ")
        for (case in cases) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(biddingPower, case)
            assert(moveIndex == 2)
        }
    }

    @Test
    fun getMoveIndex_playerHasOneDown() {
        val biddingPower = 150
        val cases = arrayOf("X        ", "   X     ", "      X  ")
        val expected = arrayOf(1, 4, 7)
        for (case in cases.zip(expected)) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(biddingPower, case.first)
            assert(moveIndex == case.second)
        }
    }

    /* Validity tests */

    @Test
    fun getBidAmt() {
        for (i in 0..1000) {
            val bidAmt = smartNormalDistPlayer.getBidAmt(100, "         ")
            assert(bidAmt in 0..100)
        }
    }

    @Test
    fun getMoveIndex_noMoves() {
        for (i in 0..100) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(5, "         ")
            assert(moveIndex in 0..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveX() {
        for (i in 0..100) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(5, "X        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveO() {
        for (i in 0..100) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(5, "O        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftXs() {
        for (i in 0..100) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(5, "XXXX XXXX")
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftOs() {
        for (i in 0..100) {
            val moveIndex = smartNormalDistPlayer.getMoveIndex(5, "OOOO OOOO")
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
            val moveIndex = smartNormalDistPlayer.getMoveIndex(biddingPower, case.first)
            assert(moveIndex == case.second)
        }
    }
}