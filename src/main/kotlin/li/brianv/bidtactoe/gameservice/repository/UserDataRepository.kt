package li.brianv.bidtactoe.gameservice.repository

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import li.brianv.bidtactoe.gameservice.exceptions.EmailAlreadyExistsException
import li.brianv.bidtactoe.gameservice.exceptions.UsernameAlreadyExistsException
import li.brianv.bidtactoe.gameservice.model.user.DEFAULT_RATING
import li.brianv.bidtactoe.gameservice.model.user.NewUser
import li.brianv.bidtactoe.gameservice.model.user.User
import li.brianv.bidtactoe.gameservice.mongo.MongoConnectionService
import org.bson.Document
import org.mindrot.jbcrypt.BCrypt.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import kotlin.reflect.full.memberProperties

@Repository
class UserDataRepository(private val mongoConnectionService: MongoConnectionService) : UserRepository {
    private val databaseName = "bidtactoe"
    private val userCollectionName = "user"
    val logger: Logger = LoggerFactory.getLogger(UserDataRepository::class.java.simpleName)

    override fun createUser(username: String, email: String, password: String) {
        val userCollection = getUserCollection()
        indexCollection(userCollection)
        val hashedPassword = hashpw(password, gensalt())
        val newUser = NewUser(username, email, hashedPassword, DEFAULT_RATING)
        val newUserDocument = Document()
        for (component in NewUser::class.memberProperties) {
            newUserDocument.append(component.name, component.get(newUser))
        }
        try {
            userCollection.insertOne(newUserDocument)
        } catch (e: MongoWriteException) {
            if (userCollection.find(Document("email", email)).count() > 0) {
                throw EmailAlreadyExistsException()
            } else if (userCollection.find(Document("username", username)).count() > 0) {
                throw UsernameAlreadyExistsException()
            }
        }
    }

    override fun authenticate(email: String, password: String): User? {
        val userCollection = getUserCollection()
        indexCollection(userCollection)
        return userCollection.find(eq("email", email))
                .filter { document -> checkpw(password, document.getString("password")) }
                .map { document -> User(document.getString("username"), document.getInteger("rating")) }
                .firstOrNull()
    }

    private fun getUserCollection(): MongoCollection<Document> {
        val mongoClient = mongoConnectionService.getAtlasMongoClient()
        val mongoDatabase = mongoClient.getDatabase(databaseName)
        return mongoDatabase.getCollection(userCollectionName)
    }

    private fun indexCollection(userCollection: MongoCollection<Document>) {
        if (userCollection.listIndexes().count() == 0) {
            userCollection.createIndex(Indexes.ascending("username"), IndexOptions().unique(true))
            userCollection.createIndex(Indexes.ascending("email"), IndexOptions().unique(true))
        }
    }
}

//fun Document.createDocumentFromModel(model: Any) {
//    TODO("Generalize building models from reflection")
//}