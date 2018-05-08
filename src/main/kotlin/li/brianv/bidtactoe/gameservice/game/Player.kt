package li.brianv.bidtactoe.gameservice.game

class Player(val username: String,
             val deviceToken: String,
             var currentBid: Int,
             var biddingPower: Int) {

    val DEFAULT_BID: Int = -1
    fun hasBid(): Boolean {
        return currentBid != DEFAULT_BID
    }

    fun gainBiddingPower(bidAmt: Int) {
        biddingPower += bidAmt
    }

    fun applyBid() {
        biddingPower -= currentBid
    }

    fun resetBid() {
        currentBid = DEFAULT_BID
    }


}