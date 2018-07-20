package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.game.GameManager
import li.brianv.bidtactoe.gameservice.repository.AIDataMongoRepository
import li.brianv.bidtactoe.gameservice.repository.FrozenAIDataMongoRepository
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import org.riversun.fcm.FcmClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import java.util.*

@SpringBootApplication
@EnableAutoConfiguration(exclude = [(MongoAutoConfiguration::class)])
class GameServiceApplication {
    @Bean
    fun provideGameManager(gameFCMComponent: GameFCMComponent,
                           gameWSComponent: GameWSComponent,
                           @Qualifier("aiDataMongoRepository") aiDataMongoRepository: AIDataMongoRepository,
                           @Qualifier("frozenAIDataMongoRepository") frozenAIDataMongoRepository: FrozenAIDataMongoRepository): GameManager {
        return GameManager(LinkedList(), ArrayList(), HashMap(), gameFCMComponent, gameWSComponent,
                aiDataMongoRepository, frozenAIDataMongoRepository)
    }

    @Bean
    fun provideFCMClient(): FcmClient {
        val client = FcmClient()
        client.setAPIKey("AIzaSyAZO6tzLHSrCJw6WV44CPnd-xFMlZzueG4")
        return client
    }

    @Bean(name = ["driverTaskScheduler"])
    fun provideTaskScheduler(): ConcurrentTaskScheduler {
        return ConcurrentTaskScheduler()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(GameServiceApplication::class.java, *args)
}