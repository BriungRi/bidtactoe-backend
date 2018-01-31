package li.brianv.bidtactoe.gameservice.repository

import li.brianv.bidtactoe.gameservice.model.User

interface UserRepository {
    fun createUser(username: String, email: String, password: String)

    fun authenticate(email: String, password: String): User?
}