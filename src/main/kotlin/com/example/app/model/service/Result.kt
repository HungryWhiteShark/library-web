package com.example.app.model.service


data class Result(
    var message: String = "", var code: Int = 100, var data: Any? = null) {
    val success: Boolean get() = code == 100
}
