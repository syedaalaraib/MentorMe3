package com.laraib.i210865

data class Chat(
    var sender: String,
    var receiver: String,
    var message: String,
    var audioUrl: String? = null,
    var imageUrl: String? = null,
    var key: String = ""
) {
    constructor() : this("", "", "")
}
