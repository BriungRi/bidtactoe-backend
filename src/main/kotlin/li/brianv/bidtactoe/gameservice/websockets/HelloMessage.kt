package li.brianv.bidtactoe.gameservice.websockets

class HelloMessage {

    var name: String? = null

    constructor() {}

    constructor(name: String) {
        this.name = name
    }
}
