package li.brianv.bidtactoe.gameservice.model.messages

data class WinnerUpdateMessage(val winnerUsername: String) {
    val type: String = "WINNER_UPDATE_MESSAGE"
}
