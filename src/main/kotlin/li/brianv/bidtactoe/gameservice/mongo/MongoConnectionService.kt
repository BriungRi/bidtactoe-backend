package li.brianv.bidtactoe.gameservice.mongo

import com.mongodb.MongoClient
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
        mongoClient = MongoClient("localhost", 27017)
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
