package li.brianv.bidtactoe.gameservice.model

data class GameReadyMessage(val gameIndex: String, val playerOneId: String, val playerTwoId: String)