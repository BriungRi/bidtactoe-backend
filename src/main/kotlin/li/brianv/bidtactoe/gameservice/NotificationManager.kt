package li.brianv.bidtactoe.gameservice

import li.brianv.bidtactoe.gameservice.firebase.GameFCMComponent
import li.brianv.bidtactoe.gameservice.websockets.GameWSComponent
import org.springframework.stereotype.Component

class NotificationManager(val gameFCMComponent: GameFCMComponent, val gameWSComponent: GameWSComponent) {
    fun gameReadyUpdate() {
    }

    fun bidsCompletedUpdate() {

    }

    fun moveUpdate() {

    }

    fun victoryUPdate() {

    }
}