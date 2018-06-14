package li.brianv.bidtactoe.gameservice.repository

import li.brianv.bidtactoe.gameservice.redis.RedisConnectionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

private const val BID_KEY_PREFIX = "q:bid"
private const val MOVE_KEY_PREFIX = "q:move"
private const val DEFAULT_Q_VALUE = 0.0

@Repository
class AIDataRepository(private val redisConnectionService: RedisConnectionService) : AIRepository {

    val logger: Logger = LoggerFactory.getLogger(AIDataRepository::class.java.simpleName)

    override fun getQValue(key: String): Double {
        val jedisPool = redisConnectionService.getJedisPool()
        var qValue = DEFAULT_Q_VALUE
        jedisPool.resource.use {
            val result = it.get(key)
            qValue = if (result == null) {
                it.set(key, DEFAULT_Q_VALUE.toString())
                DEFAULT_Q_VALUE
            } else
                result.toDouble()
        }
        return qValue
    }

    override fun getBidQValue(biddingPower: Int, cells: String, bidAmt: Int): Double {
        val jedisPool = redisConnectionService.getJedisPool()
        var qValue = DEFAULT_Q_VALUE
        jedisPool.resource.use {
            val result = it.get("$BID_KEY_PREFIX:$biddingPower:$cells:$bidAmt")
            qValue = if (result == null) {
                it.set("$BID_KEY_PREFIX:$biddingPower:$cells:$bidAmt", DEFAULT_Q_VALUE.toString())
                DEFAULT_Q_VALUE
            } else
                result.toDouble()
        }
        return qValue
    }

    override fun getMoveQValue(biddingPower: Int, cells: String, nextCells: String): Double {
        val jedisPool = redisConnectionService.getJedisPool()
        var qValue = DEFAULT_Q_VALUE
        jedisPool.resource.use {
            val result = it.get("$MOVE_KEY_PREFIX:$biddingPower:$cells:$nextCells")
            qValue = if (result == null) {
                it.set("$MOVE_KEY_PREFIX:$biddingPower:$cells:$nextCells", DEFAULT_Q_VALUE.toString())
                DEFAULT_Q_VALUE
            } else
                result.toDouble()
        }
        return qValue
    }

    override fun incrBidQValue(key: String, incrAmt: Double) {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            it.incrByFloat(key, incrAmt).toDouble()
        }
    }

    override fun incrMoveQValue(key: String, incrAmt: Double) {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            it.incrByFloat(key, incrAmt).toDouble()
        }
    }

    override fun incrWins() {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            it.incr("q:numWins")
        }
    }

    override fun incrNumGames() {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            it.incr("q:numGames")
        }
    }

}
