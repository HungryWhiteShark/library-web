package com.example.app.model.dto


data class BookInfoRequest(
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val bookDescription: String = "",
    val bookLanguage: String = "",
    val bookIsbn: String = "",
    val bookGenres: String = "",
    val bookFormat: String = "",
    val bookEdition: String = "",
    val bookPublisher: String = "",
    val bookPublishDate: String = ""
)



data class BookInfoResponse(
    val bookId: Long = 0L,
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val bookDescription: String = "",
    val bookLanguage: String = "",
    val bookIsbn: String = "",
    val bookGenres: String = "",
    val bookFormat: String = "",
    val bookEdition: String = "",
    val bookPublisher: String = "",
    val bookPublishDate: String = ""
)
