package li.brianv.bidtactoe.gameservice.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import li.brianv.bidtactoe.gameservice.exceptions.MongoNotYetAvailableException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.Future

private const val REFRESH_DELAY_IN_MILLIS = 5000L

@Service
class MongoConnectionService(@Qualifier("driverTaskScheduler") taskScheduler: TaskScheduler) {
    private var mongoConnectionTask: Future<*>? = null
    private var mongoClient: MongoClient? = null

    fun getMongoClient(): MongoClient {
        if (mongoClient != null)
            return mongoClient as MongoClient
        else
            throw MongoNotYetAvailableException()
    }

    private fun connectToMongo() {
        val uri = MongoClientURI("mongodb+srv://brian:mZGEpxV46WlZCWrL@bidtactoe-svmnm.mongodb.net/")
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
        }, REFRESH_DELAY_IN_MILLIS)
    }
}
