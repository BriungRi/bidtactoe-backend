package li.brianv.bidtactoe.gameservice.mongo

import com.nhaarman.mockito_kotlin.mock
import li.brianv.bidtactoe.gameservice.exceptions.RedisNotYetAvailableException
import li.brianv.bidtactoe.gameservice.redis.RedisConnectionService
import org.junit.Test
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import kotlin.math.roundToInt

class RedisConnectionServiceTest {

    @Test(expected = RedisNotYetAvailableException::class)
    fun getRedisClient() {
        val redisConnectionService = RedisConnectionService(mock())
        Thread.sleep(1000)
        redisConnectionService.getJedisPool()
    }
}