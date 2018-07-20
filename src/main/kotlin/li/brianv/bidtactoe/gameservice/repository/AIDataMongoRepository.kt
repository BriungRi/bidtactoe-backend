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
import org.springframework.stereotype.Repository
import java.util.*

private const val KEY_PREFIX = "q"

@Repository(value = "aiDataMongoRepository")
class AIDataMongoRepository(private val mongoConnectionService: MongoConnectionService) : AIRepository {
    private val bidKeyPrefix = "bid"
    private val moveKeyPrefix = "move"
    private val numWinsPrefix = "numWins"
    private val numGamesPrefix = "numGames"
    private val numEvalWinsKey = "qEval:numWins"
    private val numEvalTiesKey = "qEval:numTies"
    private val numEvalLossesKey = "qEval:numLosses"
    private val defaultQValue = 0.0
    private val aiDatabaseName = "ai"
    private val keyString = "key"
    private val valueString = "val"

    val logger: Logger = LoggerFactory.getLogger(AIDataMongoRepository::class.java.simpleName)

    override fun getQValue(key: String): Double {
        val mongoCollection = getMongoCollection()
        val result = mongoCollection.find(eq(keyString, key)).first()
        return if (result == null) {
            mongoCollection.insertOne(Document(keyString, key).append(valueString, 0.0))
            defaultQValue
        } else
            result.getDouble(valueString)
    }

    override fun getBestBidAmtByQValue(biddingPower: Int, cells: String): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val bidRange = 0..biddingPower
        val keys = getAllRotationsOfCells(cells).zip(bidRange)
                .map { (cellRotation, bidAmt) -> eq(keyString, "$KEY_PREFIX:$bidKeyPrefix:$biddingPower:$cellRotation:$bidAmt") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(valueString))
                .first()
        return if (documentWithBestQValue != null) {
            Pair(documentWithBestQValue.getString(keyString).split(":")[4].toInt(), documentWithBestQValue.getDouble(valueString)) // TODO: Hardcode
        } else
            Pair((Math.random() * (biddingPower + 1)).toInt(), 0.0)
    }

    override fun getBestOpenPositionByQValue(biddingPower: Int, cells: String, openPositions: List<Int>, isPlayerOne: Boolean): Pair<Int, Double> {
        val mongoCollection = this.getMongoCollection()
        val keys = getAllRotationsOfCells(cells).zip(openPositions)
                .map { (cellRotation, openPosition) -> eq(keyString, "$KEY_PREFIX:$moveKeyPrefix:$biddingPower:$cellRotation:$openPosition") }
                .toTypedArray()
        val documentWithBestQValue = mongoCollection.find(or(*keys))
                .sort(descending(valueString))
                .first()
        return if (documentWithBestQValue != null) {
            val components = documentWithBestQValue.getString(keyString).split(":")
            val rotatedCells = components[3]
            val openPosition = components[4].toInt()
            val correctOpenPosition = getCorrectOpenPosition(cells, rotatedCells, openPosition)
            Pair(correctOpenPosition, documentWithBestQValue.getDouble(valueString))
        } else
            Pair(openPositions.shuffled().first(), 0.0)
    }

    private fun getCorrectOpenPosition(originalCells: String, rotatedCells: String, openPosition: Int): Int {
        return getAllRotationsOfCells(originalCells).zip(0..3)
                .filter { (rotation, _) -> rotation == rotatedCells }
                .map { (_, index) ->
                    var position = openPosition
                    for (i in 0..index) {
                        position = getRotatedIndex(position)
                    }
                    position
                }
                .first()
    }

    private fun getAllRotationsOfCells(rotationOne: String): List<String> {
        val rotationTwo = getCellsRotatedOnce(rotationOne)
        val rotationThree = getCellsRotatedOnce(rotationTwo)
        val rotationFour = getCellsRotatedOnce(rotationThree)
        return arrayListOf(rotationOne, rotationTwo, rotationThree, rotationFour)
    }

    private fun getCellsRotatedOnce(cells: String): String {
        return "${cells[6]}${cells[3]}${cells[0]}${cells[7]}${cells[4]}${cells[1]}${cells[8]}${cells[5]}${cells[2]}"
    }

    private fun getRotatedIndex(index: Int): Int {
        return when (index) {
            6 -> 0
            3 -> 1
            0 -> 2
            7 -> 3
            4 -> 4
            1 -> 5
            8 -> 6
            5 -> 7
            2 -> 8
            else -> -1
        }
    }

    override fun incrQValues(keyToIncrAmt: Map<String, Double>) {
        val mongoCollection = this.getMongoCollection()
        val writes = ArrayList<WriteModel<Document>>(keyToIncrAmt.size)
        for ((key, incAmt) in keyToIncrAmt) {
            writes.add(UpdateOneModel<Document>(eq(keyString, key),
                    inc(valueString, incAmt),
                    UpdateOptions().upsert(true)))
        }
        if (writes.isNotEmpty())
            mongoCollection.bulkWrite(writes)
    }

    override fun incrNumWins() {
        incrValueByKeyName("$KEY_PREFIX:$numWinsPrefix")
    }

    override fun incrNumGames() {
        incrValueByKeyName("$KEY_PREFIX:$numGamesPrefix")
    }

    override fun getNumWins(): Int {
        return getValueByKeyName("$KEY_PREFIX:$numWinsPrefix")
    }

    override fun getNumGames(): Int {
        return getValueByKeyName("$KEY_PREFIX:$numGamesPrefix")
    }

    override fun incrNumEvalWins() {
        incrValueByKeyName(numEvalWinsKey)
    }

    override fun incrNumEvalTies() {
        incrValueByKeyName(numEvalTiesKey)
    }

    override fun incrNumEvalLosses() {
        incrValueByKeyName(numEvalLossesKey)
    }

    override fun getNumEvalWins(): Int {
        return getValueByKeyName(numEvalWinsKey)
    }

    override fun getNumEvalTies(): Int {
        return getValueByKeyName(numEvalTiesKey)
    }

    override fun getNumEvalLosses(): Int {
        return getValueByKeyName(numEvalLossesKey)
    }

    private fun incrValueByKeyName(keyName: String) {
        val mongoCollection = this.getMongoCollection()
        mongoCollection.updateOne(eq(keyString, keyName),
                inc(valueString, 1),
                UpdateOptions().upsert(true))
    }

    private fun getValueByKeyName(keyName: String): Int {
        val mongoCollection = this.getMongoCollection()
        val numWinsDoc = mongoCollection.find(Document(keyString, keyName))
                .first()
        return if (numWinsDoc != null)
            numWinsDoc.getInteger(valueString)
        else
            0
    }

    private fun getMongoCollection(): MongoCollection<Document> {
        val database = mongoConnectionService.getLocalMongoClient()
                .getDatabase(aiDatabaseName)
        val qLearningCollectionName = getMongoCollectionName()
        if (!database.listCollectionNames().contains(qLearningCollectionName)) {
            database.createCollection(qLearningCollectionName)
            val collection = database.getCollection(qLearningCollectionName)
            collection.createIndex(ascending(keyString))
        }
        return database.getCollection(qLearningCollectionName)

    }

    fun getMongoCollectionName(): String {
        return "qLearning"
    }
}

