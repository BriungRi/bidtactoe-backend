package li.brianv.bidtactoe.gameservice.game.player

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.brianv.bidtactoe.gameservice.repository.AIRepository
import org.junit.Test

class QLearningPlayerTest {

    private val aiRepository = mock<AIRepository> {
        on { getBestBidAmtByQValue(any(), any()) } doReturn Pair(0, 0.0)
        on { getBestOpenPositionByQValue(any(), any(), any(), any()) } doReturn Pair(4, 0.0)
    }
    private val qLearningPlayer = QLearningPlayer(aiRepository, mock(), mock())

    @Test
    fun getBidAmt() {
        for (i in 0..1000) {
            val bidAmt = qLearningPlayer.getBidAmt(100, "         ")
            assert(bidAmt in 0..100)
        }
    }


}