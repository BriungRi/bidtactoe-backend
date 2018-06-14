package li.brianv.bidtactoe.gameservice.redis

import li.brianv.bidtactoe.gameservice.exceptions.RedisNotYetAvailableException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.util.concurrent.Future

private const val REFRESH_DELAY_IN_MILLIS = 5000L

@Service
class RedisConnectionService(@Qualifier("driverTaskScheduler") taskScheduler: TaskScheduler) {
    private var redisConnectionTask: Future<*>? = null
    private var jedisPool: JedisPool? = null

    fun getJedisPool(): JedisPool {
        if (jedisPool != null)
            return jedisPool as JedisPool
        else
            throw RedisNotYetAvailableException()
    }

    private fun connectToRedis() {
        jedisPool = JedisPool(JedisPoolConfig(), "http://tactoe.bid")
    }

    init {
        redisConnectionTask = taskScheduler.scheduleWithFixedDelay({
            try {
                if (jedisPool == null) {
                    connectToRedis()
                } else {
                    redisConnectionTask?.cancel(true)
                }
            } catch (e: Exception) {
            }
        }, REFRESH_DELAY_IN_MILLIS)
    }
}
