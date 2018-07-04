package li.brianv.bidtactoe.gameservice.repository

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.or
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.Sorts.descending
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.inc
import com.mongodb.client.model.WriteModel
import li.brianv.bidtactoe.gameservice.mongo.MongoConnectionService
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private const val BID_KEY_PREFIX = "q:bid"
private const val MOVE_KEY_PREFIX = "q:move"
private const val NUM_WINS_KEY = "q:numWins"
private const val NUM_GAMES_KEY = "q:numGames"
private const val NUM_EVAL_WINS_KEY = "qEval:numWins"
private const val NUM_EVAL_TIES_KEY = "qEval:numTies"
private const val NUM_EVAL_LOSSES_KEY = "qEval:numLosses"
private const val DEFAULT_Q_VALUE = 0.0
private const val AI_DATABASE_NAME = "ai"
private const val Q_LEARNING_COLLECTION_NAME = "qLearning"
private const val KEY_NAME = "key"
private const val VALUE_NAME = "val"

class AIDataMongoRepository(private val mongoConnectionService: MongoConnectionService) : AIRepository {

    val logger: Logger = LoggerFactory.getLogger(AIDataMongoRepository::class.java.simpleName)

    override fun getQValue(key: String): Double {
        val mongoCollection = getMongoCollection()
        val result = mongoCollection.find(eq(KEY_NAME, key)).first()
        return if (result == null) {
            mongoCollection.insertOne(Document(KEY_NAME, key).append(VALUE_NAME, 0.0))
            DEFAULT_Q_VALUE
        } else
            result.getDouble(VALUE_NAME)
    }

    override fun getBestBidAmtByQValue(biddingPower: Int, cells: String): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val bidRange = 0..biddingPower
        val keys = bidRange.map { bidAmt -> eq(KEY_NAME, "$BID_KEY_PREFIX:$biddingPower:$cells:$bidAmt") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(VALUE_NAME))
                .first()
        return if (documentWithBestQValue != null) {
            Pair(documentWithBestQValue.getString(KEY_NAME).split(":")[4].toInt(), documentWithBestQValue.getDouble(VALUE_NAME)) // TODO: Hardcode
        } else
            Pair(0, 0.0)
    }

    override fun getBestOpenPositionByQValue(biddingPower: Int, cells: String, openPositions: List<Int>, isPlayerOne: Boolean): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val keys = openPositions.map { openPosition -> eq(KEY_NAME, "$MOVE_KEY_PREFIX:$biddingPower:$cells:$openPosition") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(VALUE_NAME))
                .first()
        return if (documentWithBestQValue != null) {
            Pair(documentWithBestQValue.getString(KEY_NAME).split(":")[4].toInt(), documentWithBestQValue.getDouble(VALUE_NAME)) // TODO: Hardcode
        } else
            Pair(openPositions.first(), 0.0)
    }

    override fun incrQValues(keyToIncrAmt: Map<String, Double>) {
        val mongoCollection = this.getMongoCollection()
        val writes = ArrayList<WriteModel<Document>>(keyToIncrAmt.size)
        for ((key, incAmt) in keyToIncrAmt) {
            writes.add(UpdateOneModel<Document>(eq(KEY_NAME, key),
                    inc(VALUE_NAME, incAmt),
                    UpdateOptions().upsert(true)))
        }
        if (writes.isNotEmpty())
            mongoCollection.bulkWrite(writes)
    }

    override fun incrNumWins() {
        incrValueByKeyName(NUM_WINS_KEY)
    }

    override fun incrNumGames() {
        incrValueByKeyName(NUM_GAMES_KEY)
    }

    override fun getNumWins(): Int {
        return getValueByKeyName(NUM_WINS_KEY)
    }

    override fun getNumGames(): Int {
        return getValueByKeyName(NUM_GAMES_KEY)
    }


    override fun incrNumEvalWins() {
        incrValueByKeyName(NUM_EVAL_WINS_KEY)
    }

    override fun incrNumEvalTies() {
        incrValueByKeyName(NUM_EVAL_TIES_KEY)
    }

    override fun incrNumEvalLosses() {
        incrValueByKeyName(NUM_EVAL_LOSSES_KEY)
    }

    override fun getNumEvalWins(): Int {
        return getValueByKeyName(NUM_EVAL_WINS_KEY)
    }

    override fun getNumEvalTies(): Int {
        return getValueByKeyName(NUM_EVAL_TIES_KEY)
    }

    override fun getNumEvalLosses(): Int {
        return getValueByKeyName(NUM_EVAL_LOSSES_KEY)
    }

    private fun incrValueByKeyName(keyName: String) {
        val mongoCollection = this.getMongoCollection()
        mongoCollection.updateOne(eq(KEY_NAME, keyName),
                inc(VALUE_NAME, 1),
                UpdateOptions().upsert(true))
    }

    private fun getValueByKeyName(keyName: String): Int {
        val mongoCollection = this.getMongoCollection()
        val numWinsDoc = mongoCollection.find(Document(KEY_NAME, keyName))
                .first()
        return if (numWinsDoc != null)
            numWinsDoc.getInteger(VALUE_NAME)
        else
            0
    }

    private fun getMongoCollection(): MongoCollection<Document> {
        val database = mongoConnectionService.getLocalMongoClient()
                .getDatabase(AI_DATABASE_NAME)
        if (!database.listCollectionNames().contains(Q_LEARNING_COLLECTION_NAME)) {
            database.createCollection(Q_LEARNING_COLLECTION_NAME)
            val collection = database.getCollection(Q_LEARNING_COLLECTION_NAME)
            collection.createIndex(ascending(KEY_NAME))
        }
        return database.getCollection(Q_LEARNING_COLLECTION_NAME)

    }
}
