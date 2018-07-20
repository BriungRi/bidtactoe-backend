package li.brianv.bidtactoe.gameservice.game.player

import li.brianv.bidtactoe.gameservice.game.player.ai.NormalDistPlayer
import org.junit.Test

class NormalDistPlayerTest {

    private val normalDistPlayer = NormalDistPlayer()

    @Test
    fun getBidAmt() {
        for (i in 0..1000) {
            val bidAmt = normalDistPlayer.getBidAmt(100, "         ")
            assert(bidAmt in 0..100)
        }
    }

    @Test
    fun getMoveIndex_noMoves() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "         ")
            assert(moveIndex in 0..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveX() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "X        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveO() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "O        ")
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftXs() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "XXXX XXXX")
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftOs() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "OOOO OOOO")
            assert(moveIndex == 4)
        }
    }
}