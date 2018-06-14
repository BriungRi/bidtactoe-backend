package li.brianv.bidtactoe.gameservice.repository

interface AIRepository {

    fun getQValue(key: String): Double

    fun getBidQValue(biddingPower: Int, cells: String, bidAmt: Int): Double

    fun getMoveQValue(biddingPower: Int, cells: String, nextCells: String): Double

    fun incrBidQValue(key: String, incrAmt: Double)

    fun incrMoveQValue(key: String, incrAmt: Double)

    fun incrWins()

    fun incrNumGames()
}