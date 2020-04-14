package ec.richardnarvaez.chatf.notifications

class Data {
    var title: String = ""
    var body: String = ""

    constructor()

    constructor(title: String, body: String) {
        this.title = title
        this.body = body
    }
}