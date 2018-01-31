package li.brianv.bidtactoe.gameservice.repository

import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import li.brianv.bidtactoe.gameservice.exceptions.EmailAlreadyExistsException
import li.brianv.bidtactoe.gameservice.exceptions.UsernameAlreadyExistsException
import li.brianv.bidtactoe.gameservice.model.DEFAULT_RATING
import li.brianv.bidtactoe.gameservice.model.NewUser
import li.brianv.bidtactoe.gameservice.model.User
import li.brianv.bidtactoe.gameservice.model.UserCredentials
import li.brianv.bidtactoe.gameservice.mongo.MongoConnectionService
import org.bson.Document
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
        logger.info("createUser()")
        val userCollection = getUserCollection()
        indexCollection(userCollection)
        val newUser = NewUser(username, email, password, DEFAULT_RATING)
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
        val userCredentials = UserCredentials(email, password)
        val findUser = Document()
        for (component in UserCredentials::class.memberProperties) {
            findUser.append(component.name, component.get(userCredentials))
        }
        return userCollection.find(findUser)
                .map({ document -> User(document.getString("username"), document.getInteger("rating")) })
                .first()
    }

    private fun getUserCollection(): MongoCollection<Document> {
        val mongoClient = mongoConnectionService.getMongoClient()
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