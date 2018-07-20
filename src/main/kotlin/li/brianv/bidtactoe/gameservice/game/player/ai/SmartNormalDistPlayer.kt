package li.brianv.bidtactoe.gameservice.game.player.ai

open class SmartNormalDistPlayer : NormalDistPlayer() {

    override fun getAICode(): String {
        return "SNDP"
    }

    /**
     * If the other player's about to win, outbid them to block
     * Else, if player is about to win, bid all
     * Else, random bid
     */
    override fun getBidAmt(biddingPower: Int, cells: String): Int {
        getWinningBid(biddingPower, cells)?.let { return it }
        getBlockingBid(biddingPower, cells)?.let { return it }
        return super.getBidAmt(biddingPower, cells)
    }

    /**
     * If there's two in a row, pick the third spot
     * Else, if the opponent has two in a row, block them
     * Else, if there's a group of 3 with only one of the player's pieces, pick one of those spots
     * Else, random move
     */
    override fun getMoveIndex(biddingPower: Int, cells: String): Int {
        getWinningMoveIndex(cells)?.let { return it }
        getBlockingMoveIndex(cells)?.let { return it }
        getConsecutiveMoveIndex(cells)?.let { return it }
        getMiddleIndex(cells)?.let { return it }
        getCornerIndex(cells)?.let { return it }
        return super.getMoveIndex(biddingPower, cells)
    }


}