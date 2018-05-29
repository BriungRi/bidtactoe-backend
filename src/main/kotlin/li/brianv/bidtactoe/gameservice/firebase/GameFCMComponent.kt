package li.brianv.bidtactoe.gameservice.firebase

import org.riversun.fcm.FcmClient
import org.riversun.fcm.model.EntityMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

const val GAME_INDEX_KEY: String = "gameIndex"
const val PLAYER_ONE_KEY: String = "playerOneUsername"
const val PLAYER_TWO_KEY: String = "playerTwoUsername"
const val BID_WINNER_KEY: String = "bidWinner"
const val BID_POWER_KEY: String = "bidPower"
const val CELLS_KEY: String = "cells"
const val GAME_WINNER_KEY = "gameWinner"

@Component
class GameFCMComponent(val fcmClient: FcmClient) {

    val logger: Logger = LoggerFactory.getLogger(GameFCMComponent::class.java.simpleName)

    fun gameReadyUpdate(gameIndex: String, playerOneUsername: String, playerTwoUsername: String, deviceToken: String) {
        Thread {
            Thread.sleep(1000)
            logger.info("gameReadyUpdate(): " +
                    "gameIndex: $gameIndex, " +
                    "playerOneUsername: $playerOneUsername, " +
                    "playerTwoUsername: $playerTwoUsername, " +
                    "deviceToken: $deviceToken")
            sendMessage("$GAME_INDEX_KEY,$PLAYER_ONE_KEY,$PLAYER_TWO_KEY",
                    "$gameIndex,$playerOneUsername,$playerTwoUsername",
                    deviceToken)
        }.run()
    }

    fun bidsCompletedUpdate(bidWinnerUsername: String, biddingPower: String, deviceToken: String) {
        sendMessage("$BID_WINNER_KEY,$BID_POWER_KEY",
                "$bidWinnerUsername,$biddingPower",
                deviceToken)
    }

    fun moveUpdate(cells: String, vararg deviceTokens: String) {
        sendMessage(CELLS_KEY, cells, *deviceTokens)
    }

    fun victoryUpdate(winnerUsername: String, vararg deviceTokens: String) {
        sendMessage(GAME_WINNER_KEY, winnerUsername, *deviceTokens)
    }

    private fun sendMessage(key: String, value: String, vararg deviceTokens: String) {
        for (deviceToken in deviceTokens) {
            fcmClient.pushToEntities(buildMessage(deviceToken, key, value))
        }
    }

    private fun buildMessage(deviceToken: String, key: String, value: String): EntityMessage {
        val msg = EntityMessage()
        msg.addRegistrationToken(deviceToken)
        msg.putStringData(key, value)
        return msg
    }
}