package li.brianv.bidtactoe.gameservice.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import li.brianv.bidtactoe.gameservice.exceptions.MongoNotYetAvailableException
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.Future

@Service
class MongoConnectionService(taskScheduler: TaskScheduler) {
    private val REFRESH_DELAY_IN_MS = 5000L
    private var mongoConnectionTask: Future<*>? = null
    private var mongoClient: MongoClient? = null

    fun getMongoClient(): MongoClient {
        if (mongoClient != null)
            return mongoClient as MongoClient
        else
            throw MongoNotYetAvailableException()
    }

    private fun connectToMongo() {
        val uri = MongoClientURI("mongodb+srv://brian:crazyowls123!@bidtactoe-svmnm.mongodb.net/")
        mongoClient = MongoClient(uri)
    }

    init {
        mongoConnectionTask = taskScheduler.scheduleWithFixedDelay({
            try {
                if (mongoClient == null) {
                    connectToMongo()
                } else {
                    mongoConnectionTask?.cancel(true)
                }

            } catch (e: Exception) {
            }
        }, REFRESH_DELAY_IN_MS)
    }
}
