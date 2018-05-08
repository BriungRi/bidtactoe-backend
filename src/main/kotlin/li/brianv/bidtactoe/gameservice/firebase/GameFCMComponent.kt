package li.brianv.bidtactoe.gameservice.firebase

import org.riversun.fcm.FcmClient
import org.riversun.fcm.model.EntityMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameFCMComponent(val fcmClient: FcmClient) {

    val logger: Logger = LoggerFactory.getLogger(GameFCMComponent::class.java.simpleName)
    internal val GAME_INDEX_KEY: String = "gameIndex"
    internal val PLAYER_ONE_KEY: String = "playerOneId"
    internal val BID_WINNER_KEY: String = "bidWinner"
    internal val BID_POWER_KEY: String = "bidPower"
    internal val CELLS_KEY: String = "cells"
    internal val GAME_WINNER_KEY = "gameWinner"

    fun gameReadyUpdate(gameIndex: String, playerOneId: String, vararg deviceTokens: String) {
        logger.info("gameReadyUpdate(): gameIndex: $gameIndex, playerOneId: $playerOneId")
        sendMessage("$GAME_INDEX_KEY,$PLAYER_ONE_KEY",
                "$gameIndex,$playerOneId",
                *deviceTokens)
    }

    fun bidsCompletedUpdate(bidWinnerId: String, biddingPower: String, deviceToken: String) {
        sendMessage("$BID_WINNER_KEY,$BID_POWER_KEY",
                "$bidWinnerId,$biddingPower",
                deviceToken)
    }

    fun moveUpdate(cells: String, vararg deviceTokens: String) {
        sendMessage(CELLS_KEY, cells, *deviceTokens)
    }

    fun victoryUpdate(winnerId: String, vararg deviceTokens: String) {
        sendMessage(GAME_WINNER_KEY, winnerId, *deviceTokens)
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