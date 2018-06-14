package li.brianv.bidtactoe.gameservice.model.ai

private const val PREFIX = "q:bid:"
enum class QBidLearningFeature(val stringName: String) {
    NUM_WINNING_SPOTS(PREFIX + "winningSpots"), WEB("web"), IOS("ios")
}