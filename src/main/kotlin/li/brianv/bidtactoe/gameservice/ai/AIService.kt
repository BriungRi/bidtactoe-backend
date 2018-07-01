package li.brianv.bidtactoe.gameservice.ai

import li.brianv.bidtactoe.gameservice.game.GameManager
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.stereotype.Component
import java.util.*

const val AI_ADD_DELAY = 250L

@Component
class AIService(concurrentTaskScheduler: ConcurrentTaskScheduler, gameManager: GameManager) {
    init {
        concurrentTaskScheduler.scheduleAtFixedRate({
            gameManager.addAI()
        }, Date(Date().time + 5000), AI_ADD_DELAY)
    }
}