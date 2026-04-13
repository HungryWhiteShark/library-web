package com.example.app.controller

import com.example.app.base.BaseController
import com.example.app.model.model.BookInfoModel
import com.example.app.utils.LogUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/book")
class BookInfoController(private val bookInfoModel: BookInfoModel): BaseController() {

    @GetMapping(value = ["/book"])
    fun getBookInfo(@RequestParam title: String): ResponseEntity<Any> {
        return try {
            responseData(bookInfoModel.getBookInfo(title))
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            response(500, "system-error")
        }
    }
    
}
