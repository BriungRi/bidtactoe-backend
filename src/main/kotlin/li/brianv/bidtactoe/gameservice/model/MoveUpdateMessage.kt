package li.brianv.bidtactoe.gameservice.model

data class MoveUpdateMessage(val cells: String) {
    val type: String = "MOVE_UPDATE_MESSAGE"
}
