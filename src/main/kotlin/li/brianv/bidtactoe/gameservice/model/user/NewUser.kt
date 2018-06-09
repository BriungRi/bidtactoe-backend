package li.brianv.bidtactoe.gameservice.model.user

const val DEFAULT_RATING = 1200

data class NewUser(val username: String, val email: String, val password: String, val rating: Int)
