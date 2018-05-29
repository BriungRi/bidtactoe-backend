package li.brianv.bidtactoe.gameservice.model.messages

data class MoveUpdateMessage(val cells: String) {
    val type: String = "MOVE_UPDATE_MESSAGE"
}
