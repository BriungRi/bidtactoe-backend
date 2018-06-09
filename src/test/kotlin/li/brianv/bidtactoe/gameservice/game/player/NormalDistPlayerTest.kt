package li.brianv.bidtactoe.gameservice.game.player

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

class NormalDistPlayerTest {

    private val normalDistPlayer = NormalDistPlayer(mock())

    @Test
    fun getBidAmt() {
        for(i in 0..1000) {
            val bidAmt = normalDistPlayer.getBidAmt(100, "         ")
            assert(bidAmt in 0..100)
        }
    }

    @Test
    fun getMoveIndex_noMoves() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "        ", true)
            assert(moveIndex in 0..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveX() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "X        ", true)
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveO() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "O        ", true)
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftXs() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "XXXX XXXX", true)
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftOs() {
        for (i in 0..100) {
            val moveIndex = normalDistPlayer.getMoveIndex(5, "OOOO OOOO", true)
            assert(moveIndex == 4)
        }
    }
}