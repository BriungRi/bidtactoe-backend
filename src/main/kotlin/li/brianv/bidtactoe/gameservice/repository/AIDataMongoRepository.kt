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
private const val DEFAULT_Q_VALUE = 0.0
private const val AI_DATABASE_NAME = "ai"
private const val Q_LEARNING_COLLECTION_NAME = "qLearning"
private const val keyName = "key"
private const val valueName = "val"

class AIDataMongoRepository(private val mongoConnectionService: MongoConnectionService) : AIRepository {

    val logger: Logger = LoggerFactory.getLogger(AIDataMongoRepository::class.java.simpleName)

    override fun getQValue(key: String): Double {
        val mongoCollection = getMongoCollection()
        val result = mongoCollection.find(eq(keyName, key)).first()
        return if (result == null) {
            mongoCollection.insertOne(Document(keyName, key).append(valueName, 0.0))
            DEFAULT_Q_VALUE
        } else
            result.getDouble(valueName)
    }

    override fun getBestBidAmtByQValue(biddingPower: Int, cells: String): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val bidRange = 0..biddingPower
        val keys = bidRange.map { bidAmt -> eq(keyName, "$BID_KEY_PREFIX:$biddingPower:$cells:$bidAmt") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(valueName))
                .first()
        return if (documentWithBestQValue != null) {
            Pair(documentWithBestQValue.getString(keyName).split(":")[4].toInt(), documentWithBestQValue.getDouble(valueName)) // TODO: Hardcode
        } else
            Pair(0, 0.0)
    }

    override fun getBestOpenPositionByQValue(biddingPower: Int, cells: String, openPositions: List<Int>, isPlayerOne: Boolean): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val keys = openPositions.map { openPosition -> eq(keyName, "$MOVE_KEY_PREFIX:$biddingPower:$cells:$openPosition") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(valueName))
                .first()
        return if (documentWithBestQValue != null) {
            Pair(documentWithBestQValue.getString(keyName).split(":")[4].toInt(), documentWithBestQValue.getDouble(valueName)) // TODO: Hardcode
        } else
            Pair(openPositions.first(), 0.0)
    }

    override fun incrQValues(keyToIncrAmt: Map<String, Double>) {
        val mongoCollection = this.getMongoCollection()
        val writes = ArrayList<WriteModel<Document>>(keyToIncrAmt.size)
        for ((key, incAmt) in keyToIncrAmt) {
            writes.add(UpdateOneModel<Document>(eq(keyName, key),
                    inc(valueName, incAmt),
                    UpdateOptions().upsert(true)))
        }
        if (writes.isNotEmpty())
            mongoCollection.bulkWrite(writes)
    }

    override fun incrNumWins() {
        val mongoCollection = this.getMongoCollection()
        mongoCollection.updateOne(eq(keyName, NUM_WINS_KEY),
                inc(valueName, 1),
                UpdateOptions().upsert(true))
    }

    override fun incrNumGames() {
        val mongoCollection = this.getMongoCollection()
        mongoCollection.updateOne(eq(keyName, NUM_GAMES_KEY),
                inc(valueName, 1),
                UpdateOptions().upsert(true))
    }

    override fun getNumWins(): Int {
        val mongoCollection = this.getMongoCollection()
        val numWinsDoc = mongoCollection.find(Document(keyName, NUM_WINS_KEY))
                .first()
        return if (numWinsDoc != null)
            numWinsDoc.getInteger(valueName)
        else
            0
    }

    override fun getNumGames(): Int {
        val mongoCollection = this.getMongoCollection()
        val numGamesDoc = mongoCollection.find(Document(keyName, NUM_GAMES_KEY))
                .first()
        return if (numGamesDoc != null)
            numGamesDoc.getInteger(valueName)
        else
            1
    }

    private fun getMongoCollection(): MongoCollection<Document> {
        val database = mongoConnectionService.getLocalMongoClient()
                .getDatabase(AI_DATABASE_NAME)
        if (!database.listCollectionNames().contains(Q_LEARNING_COLLECTION_NAME)) {
            database.createCollection(Q_LEARNING_COLLECTION_NAME)
            val collection = database.getCollection(Q_LEARNING_COLLECTION_NAME)
            collection.createIndex(ascending(keyName))
        }
        return database.getCollection(Q_LEARNING_COLLECTION_NAME)

    }
}
