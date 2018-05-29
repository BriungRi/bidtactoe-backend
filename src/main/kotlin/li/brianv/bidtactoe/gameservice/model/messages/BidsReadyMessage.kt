package li.brianv.bidtactoe.gameservice.model.messages

data class BidsReadyMessage(val bidWinnerId: String, val biddingPower: Int) {
    val type: String = "BIDS_READY_MESSAGE"
}
