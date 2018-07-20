package li.brianv.bidtactoe.gameservice.repository

import com.nhaarman.mockito_kotlin.*
import li.brianv.bidtactoe.gameservice.game.EMPTY_SPACE
import li.brianv.bidtactoe.gameservice.redis.RedisConnectionService
import org.junit.Before
import org.junit.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.Transaction

private const val sampleKey = "q:bid:100:         :50"
private const val sampleValue = 0.5
private const val sampleIncrAmt = 0.5
private const val sampleBiddingPower = 100
private const val sampleCells = "         "
private const val isPlayerOne = true

class AIDataRedisRepositoryTest {
    private val transaction = mock<Transaction> {}
    private val jedis = mock<Jedis> {
        onGeneric { mget(any<String>()) } doReturn (100..200).map { it.toDouble().toString() }.toMutableList()
        on { get(sampleKey) } doReturn sampleValue.toString()
        on { multi() } doReturn transaction
    }
    private val jedisPool = mock<JedisPool> {
        on { resource } doReturn jedis
    }
    private val redisConnectionService = mock<RedisConnectionService> {
        on { getJedisPool() } doReturn jedisPool
    }
    private val aiDataRepository = AIDataRedisRepository(redisConnectionService)
    private val keyToIncrAmt = HashMap<String, Double>()

    @Before
    fun setUp() {
        keyToIncrAmt[sampleKey] = sampleIncrAmt
    }

    @Test
    fun getQValue() {
        val value = aiDataRepository.getQValue(sampleKey)
        assert(value == sampleValue)
    }

    @Test
    fun getBestBidAmtByQValue() {
        val bestBidAmtQValuePair = aiDataRepository.getBestBidAmtByQValue(sampleBiddingPower, sampleCells)
        assert(bestBidAmtQValuePair.first == 100 && bestBidAmtQValuePair.second == 200.0)
    }

    @Test
    fun getBestOpenPositionByQValue() {
        val bestOpenPositionValuePair = aiDataRepository.getBestOpenPositionByQValue(100,
                sampleCells,
                (0 until sampleCells.length).toList(),
                isPlayerOne)
        assert(bestOpenPositionValuePair.first == 8 && bestOpenPositionValuePair.second == 108.0)
    }

    @Test
    fun incrQValues() {
        aiDataRepository.incrQValues(keyToIncrAmt)
        verify(transaction, times(1)).incrByFloat(sampleKey, sampleIncrAmt)
        verify(transaction, times(1)).exec()
    }

    @Test
    fun getMoveIndex_noMoves() {
        for (i in 0..100) {
            val cells = "        "
            val openPositions = getOpenPositions(cells)
            val moveIndex = aiDataRepository.getBestOpenPositionByQValue(5, cells, openPositions, isPlayerOne).first
            assert(moveIndex in 0..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveX() {
        for (i in 0..100) {
            val cells = "X       "
            val openPositions = getOpenPositions(cells)
            val moveIndex = aiDataRepository.getBestOpenPositionByQValue(5, cells, openPositions, isPlayerOne).first
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveO() {
        for (i in 0..100) {
            val cells = "O       "
            val openPositions = getOpenPositions(cells)
            val moveIndex = aiDataRepository.getBestOpenPositionByQValue(5, cells, openPositions, isPlayerOne).first
            assert(moveIndex in 1..8)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftXs() {
        for (i in 0..100) {
            val cells = "XXXX XXXX"
            val openPositions = getOpenPositions(cells)
            val moveIndex = aiDataRepository.getBestOpenPositionByQValue(5, cells, openPositions, isPlayerOne).first
            assert(moveIndex == 4)
        }
    }

    @Test
    fun getMoveIndex_oneMoveLeftOs() {
        for (i in 0..100) {
            val cells = "OOOO OOOO"
            val openPositions = getOpenPositions(cells)
            val moveIndex = aiDataRepository.getBestOpenPositionByQValue(5, cells, openPositions, isPlayerOne).first
            assert(moveIndex == 4)
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
}