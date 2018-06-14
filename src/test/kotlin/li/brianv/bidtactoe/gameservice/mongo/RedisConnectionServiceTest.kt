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

    @Test
    fun funFunc() {
        val jedisPool = JedisPool(JedisPoolConfig(), "localhost")
        jedisPool.resource.use {
            it.set("hello", "world")
            it.set("fun", "0")
            println(it.get("fun"))
            it.incrBy("fun", -5)
            println(it.get("fun"))
            if (it.get("noExist") == null)
                println("Result " + it.get("noexist"))
            else
                println("Is null")
        }
    }

    @Test
    fun random() {
        for(i in 0..10000) {
            val num = (Math.random() * (10)).roundToInt()
            assert(num in 0..10)
        }
    }
}