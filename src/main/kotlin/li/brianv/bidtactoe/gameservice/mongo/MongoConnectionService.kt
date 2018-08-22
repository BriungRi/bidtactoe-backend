package li.brianv.bidtactoe.gameservice.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import li.brianv.bidtactoe.gameservice.exceptions.MongoNotYetAvailableException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.Future

private const val REFRESH_DELAY_IN_MILLIS = 5000L

@Service
class MongoConnectionService(@Qualifier("driverTaskScheduler") taskScheduler: TaskScheduler) {
    val logger: Logger = LoggerFactory.getLogger(MongoConnectionService::class.java.simpleName)

    private var mongoConnectionTask: Future<*>? = null
    private var atlasMongoClient: MongoClient? = null
    private var localMongoClient: MongoClient? = null

    fun getAtlasMongoClient(): MongoClient {
        if (atlasMongoClient != null)
            return atlasMongoClient as MongoClient
        else
            throw MongoNotYetAvailableException()
    }

    fun getLocalMongoClient(): MongoClient {
        if (localMongoClient != null)
            return localMongoClient as MongoClient
        else
            throw MongoNotYetAvailableException()
    }

    private fun connectToAtlasMongo() {
        try {
            logger.info("Trying to connect to remote mongo")
            val atlasURI = MongoClientURI("")
            atlasMongoClient = MongoClient(atlasURI)
        } catch (e: Exception) {
            logger.error("Unable to connect to remote mongod", e)
        }
    }

    private fun connectToLocalMongo() {
        try {
            logger.info("Trying to connect to local mongo")
            localMongoClient = MongoClient("mongo")
        } catch (e: Exception) {
            logger.error("Unable to connect to local mongod", e)
        }
    }

    init {
        mongoConnectionTask = taskScheduler.scheduleWithFixedDelay({
            try {
                if (atlasMongoClient == null || localMongoClient == null) {
                    if (atlasMongoClient == null)
                        connectToAtlasMongo()
                    if (localMongoClient == null)
                        connectToLocalMongo()
                } else {
                    mongoConnectionTask?.cancel(true)
                }
            } catch (e: Exception) {
            }
        }, REFRESH_DELAY_IN_MILLIS)
    }
}
