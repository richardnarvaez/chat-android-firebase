package ec.richardnarvaez.chatf.chat.models

/**
 * Created by macbookpro on 2/27/18.
 */

public class Message {
    var state: String = ""
    var user_uid: String = ""
    var text: String = ""
    var timestamp: Any = ""

    constructor()

    constructor(_user_uid: String, text: String, timestamp: Any, state: String) {
        this.user_uid = _user_uid
        this.text = text
        this.timestamp = timestamp
        this.state = state
    }
}
