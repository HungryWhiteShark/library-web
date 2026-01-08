package com.example.app.base



open class BaseResponse {

    var code: Int = 100
    var message: String = "success"
    var messageKey: String = "success"

    constructor(code: Int = 100, message: String = "success") {
        this.code = code
        this.message = message
        this.messageKey = message.replace("  ", " ").replace(" ", "-")
            .lowercase()

    }

    class Data<T> (var data: T?, code: Int = 100, message: String = "success"): BaseResponse(code, message)


    class DataList<T> (var data: List<T> = emptyList(), code: Int = 100, message: String = "success"): BaseResponse(code, message)

}

