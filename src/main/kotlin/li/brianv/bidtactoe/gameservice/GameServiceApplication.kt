package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.game.GameManager
import li.brianv.bidtactoe.gameservice.game.MoveMaker
import li.brianv.bidtactoe.gameservice.game.Player
import org.riversun.fcm.FcmClient
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import java.util.*

@SpringBootApplication
@EnableAutoConfiguration(exclude = arrayOf(MongoAutoConfiguration::class))
class GameServiceApplication {
    @Bean
    fun provideGameManager(moveMaker: MoveMaker, gameFCMComponent: GameFCMComponent): GameManager {
        return GameManager(LinkedList<Player>() as Queue<Player>, ArrayList(), moveMaker, gameFCMComponent)
    }

    @Bean
    fun provideFCMClient(): FcmClient {
        val client = FcmClient()
        client.setAPIKey("AIzaSyAZO6tzLHSrCJw6WV44CPnd-xFMlZzueG4")
        return client
    }

    @Bean
    fun provideTaskScheduler(): TaskScheduler {
        return ConcurrentTaskScheduler()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(GameServiceApplication::class.java, *args)
}