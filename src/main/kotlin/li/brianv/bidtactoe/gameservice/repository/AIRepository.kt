package li.brianv.bidtactoe.gameservice.repository

interface AIRepository {

    fun getQValue(key: String): Double

    fun getBestBidAmtByQValue(biddingPower: Int, cells: String): Pair<Int, Double>

    fun getBestOpenPositionByQValue(biddingPower: Int, cells: String, openPositions: List<Int>, isPlayerOne: Boolean): Pair<Int, Double>

    fun incrQValues(keyToIncrAmt: Map<String, Double>)

    fun incrNumWins()

    fun incrNumGames()

    fun getNumWins(): Int

    fun getNumGames(): Int
}