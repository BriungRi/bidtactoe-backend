package li.brianv.bidtactoe.gameservice.repository

import li.brianv.bidtactoe.gameservice.mongo.MongoConnectionService
import org.springframework.stereotype.Repository

@Repository(value = "frozenAIDataMongoRepository")
class FrozenAIDataMongoRepository(mongoConnectionService: MongoConnectionService) :
        AIDataMongoRepository(mongoConnectionService) {

    override fun getMongoCollectionName(): String {
        return "qLearningFrozen"
    }

    override fun incrQValues(keyToIncrAmt: Map<String, Double>) {
    }

    override fun incrNumWins() {
    }

    override fun incrNumGames() {
    }

    override fun incrNumEvalWins() {
    }

    override fun incrNumEvalTies() {
    }

    override fun incrNumEvalLosses() {
    }
}