package li.brianv.bidtactoe.gameservice.repository

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import li.brianv.bidtactoe.gameservice.redis.RedisConnectionService
import org.junit.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

class AIDataRepositoryTest {

    private val jedis = mock<Jedis> {
        onGeneric { mget(any<String>()) } doReturn (100..200).map { it.toDouble().toString() }.toMutableList()
    }
    private val jedisPool = mock<JedisPool> {
        on { resource } doReturn jedis
    }
    private val redisConnectionService = mock<RedisConnectionService> {
        on { getJedisPool() } doReturn jedisPool
    }
    private val aiDataRepository = AIDataRepository(redisConnectionService)

    @Test
    fun getQValue() {
    }

    @Test
    fun getMaxBidAmtByQValue() {
        val maxBidAmtQValuePair = aiDataRepository.getBestBidAmtByQValue(100, "         ")
        assert(maxBidAmtQValuePair.first == 100 && maxBidAmtQValuePair.second == 200.0)
    }

    @Test
    fun getMoveQValue() {
    }

    @Test
    fun incrBidQValue() {
    }

    @Test
    fun incrMoveQValue() {
    }

    @Test
    fun incrNumWins() {
    }

    @Test
    fun incrNumGames() {
    }

    @Test
    fun getNumWins() {
    }

    @Test
    fun getNumGames() {
    }
}