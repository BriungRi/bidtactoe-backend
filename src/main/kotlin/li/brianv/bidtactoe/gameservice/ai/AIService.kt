package li.brianv.bidtactoe.gameservice.ai

import com.mashape.unirest.http.Unirest
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.thread

const val AI_ADD_DELAY = 1000L

@Component
class AIService(concurrentTaskScheduler: ConcurrentTaskScheduler) {
    init {
//        thread {
//            Thread.sleep(5000)
//            Unirest.post("http://localhost:3001/add_ai")
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .header("Cache-Control", "no-cache")
//                    .header("Postman-Token", "de615a21-ce8c-49be-8ca9-62afb34b621b")
//                    .asString()
//            Unirest.post("http://localhost:3001/add_ai")
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .header("Cache-Control", "no-cache")
//                    .header("Postman-Token", "de615a21-ce8c-49be-8ca9-62afb34b621b")
//                    .asString()
//        }
        concurrentTaskScheduler.scheduleAtFixedRate({
            Unirest.post("http://localhost:3001/add_ai")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "de615a21-ce8c-49be-8ca9-62afb34b621b")
                    .asString()
        }, Date(Date().time + AI_ADD_DELAY), AI_ADD_DELAY)
    }
}