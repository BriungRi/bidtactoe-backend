package li.brianv.bidtactoe.gameservice.game

// TODO: can game be immutable or is it better (performance wise) that its state changes?
class Game(var cells: String, val playerOne: Player, val playerTwo: Player) {
    private val winPositions: Array<Array<Int>> = arrayOf(arrayOf(0, 1, 2),
            arrayOf(3, 4, 5),
            arrayOf(6, 7, 8),
            arrayOf(0, 3, 6),
            arrayOf(1, 4, 7),
            arrayOf(2, 5, 8),
            arrayOf(0, 4, 8),
            arrayOf(2, 4, 6))

    private val defaultValue: Char = ' '
    private val playerOneValue: Char = 'O'

    fun getWinner(): Player? {
        for (winPosition in winPositions) {
            val cellsCharArray = cells.toCharArray()
            if (cellsCharArray[winPosition[0]] != defaultValue &&
                    cellsCharArray[winPosition[0]] == cellsCharArray[winPosition[1]] &&
                    cellsCharArray[winPosition[0]] == cellsCharArray[winPosition[2]]) {
                return if (cellsCharArray[winPosition[0]] == playerOneValue)
                    playerOne
                else
                    playerTwo
            }
        }
        return null
    }

    fun getPlayerDeviceTokens(): Array<String> {
        return arrayOf(playerOne.deviceToken, playerTwo.deviceToken)
    }

    fun getPlayerUsernames(): Array<String> {
        return arrayOf(playerOne.username, playerTwo.username)
    }
}