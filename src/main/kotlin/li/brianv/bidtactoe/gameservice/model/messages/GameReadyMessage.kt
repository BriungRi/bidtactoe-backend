package li.brianv.bidtactoe.gameservice.model.messages

data class GameReadyMessage(val gameIndex: Int, val playerOneUsername: String, val playerTwoUsername: String) {
    val type: String = "GAME_READY_MESSAGE"
}