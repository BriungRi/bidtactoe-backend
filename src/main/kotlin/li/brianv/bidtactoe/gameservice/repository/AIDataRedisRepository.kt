package li.brianv.bidtactoe.gameservice.repository

import li.brianv.bidtactoe.gameservice.redis.RedisConnectionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val BID_KEY_PREFIX = "q:bid"
private const val MOVE_KEY_PREFIX = "q:move"
private const val DEFAULT_Q_VALUE = 0.0

class AIDataRedisRepository(private val redisConnectionService: RedisConnectionService) : AIRepository {

    val logger: Logger = LoggerFactory.getLogger(AIDataRedisRepository::class.java.simpleName)

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

    // TODO: Can reduce ensureKeysExist + method into a single transaction

    override fun getBestBidAmtByQValue(biddingPower: Int, cells: String): Pair<Int, Double> {
        val jedisPool = redisConnectionService.getJedisPool()
        val bidRange = 0..biddingPower
        val keys = bidRange.map { bidAmt -> "$BID_KEY_PREFIX:$biddingPower:$cells:$bidAmt" }.toTypedArray()
        ensureKeysExist(*keys)
        jedisPool.resource.use {
            val qValues = it.mget(*keys).map { it.toDouble() }.toTypedArray()
            return bidRange.zip(qValues).maxBy { it.second } ?: Pair(0, 0.0)
        }
    }

    override fun getBestOpenPositionByQValue(biddingPower: Int, cells: String, openPositions: List<Int>, isPlayerOne: Boolean): Pair<Int, Double> {
        val jedisPool = redisConnectionService.getJedisPool()
        val keys = openPositions.map { openPosition -> "$MOVE_KEY_PREFIX:$biddingPower:$cells:$openPosition" }.toTypedArray()
        ensureKeysExist(*keys)
        jedisPool.resource.use {
            val qValues = it.mget(*keys).map { it.toDouble() }.toTypedArray()
            return openPositions.zip(qValues).maxBy { it.second } ?: Pair(openPositions.first(), 0.0)
        }
    }

    private fun ensureKeysExist(vararg keys: String) {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            val transaction = it.multi()
            for (key in keys) {
                transaction.incrByFloat(key, 0.0)
            }
            transaction.exec()
        }
    }

    override fun incrQValues(keyToIncrAmt: Map<String, Double>) {
        val jedisPool = redisConnectionService.getJedisPool()
        jedisPool.resource.use {
            val transaction = it.multi()
            for ((key, incrAmt) in keyToIncrAmt)
                transaction.incrByFloat(key, incrAmt)
            transaction.exec()
        }
    }

    override fun incrNumWins() {
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

    override fun getNumWins(): Int {
        val jedisPool = redisConnectionService.getJedisPool()
        var numWins = 0
        jedisPool.resource.use {
            numWins = it.get("q:numWins").toInt()
        }
        return numWins
    }

    override fun getNumGames(): Int {
        val jedisPool = redisConnectionService.getJedisPool()
        var numGames = 0
        jedisPool.resource.use {
            numGames = it.get("q:numGames").toInt()
        }
        return numGames
    }
}
