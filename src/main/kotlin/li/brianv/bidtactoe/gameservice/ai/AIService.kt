package li.brianv.bidtactoe.gameservice.ai

import li.brianv.bidtactoe.gameservice.game.GameManager
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.stereotype.Component
import java.util.*

private const val AI_ADD_DELAY = 250L
private const val AI_ADD_START_DELAY = 10000

@Component
class AIService(concurrentTaskScheduler: ConcurrentTaskScheduler, gameManager: GameManager) {
    init {
        concurrentTaskScheduler.scheduleAtFixedRate({
            gameManager.addAI()
        }, Date(Date().time + AI_ADD_START_DELAY), AI_ADD_DELAY)
    }
}