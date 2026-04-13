package com.example.app.model.model

import com.example.app.db.BookInfo
import com.example.app.model.dto.BookInfoRequest
import com.example.app.model.dto.BookInfoResponse
import com.example.app.model.repo.BookInfoRepo
import com.example.app.utils.LogUtils
import org.springframework.stereotype.Service



@Service
class BookInfoModel(private val bookInfoRepo: BookInfoRepo) {

    fun BookInfo.toBookInfoResponse(): BookInfoResponse {
        return BookInfoResponse(
            bookId = this.bookId,
            bookTitle = this.bookTitle,
            bookLanguage = this.bookLanguage,
            bookAuthor = this.bookAuthor,
            bookDescription = this.bookDescription,
            bookGenres = this.bookGenres,
            bookPublisher = this.bookPublisher ?: "",
            bookIsbn = this.bookIsbn ?: "",
            bookFormat = this.bookFormat,
            bookEdition = this.bookEdition ?: "",
            bookPublishDate = this.bookPublishDate ?: ""
        )
    }


    fun BookInfoResponse.toBookInfo(): BookInfo {
        return BookInfo(
            bookTitle = this.bookTitle,
            bookLanguage = this.bookLanguage,
            bookAuthor = this.bookAuthor,
            bookDescription = this.bookDescription,
            bookGenres = this.bookGenres,
            bookPublisher = this.bookPublisher,
            bookIsbn = this.bookIsbn,
            bookFormat = this.bookFormat,
            bookEdition = this.bookEdition,
            bookPublishDate = this.bookPublishDate
        )
    }


    fun getBookInfo(title: String): List<BookInfoResponse> {
        return try {
            val res = bookInfoRepo.getBookInfo(title)
            return res.ifEmpty { emptyList() }
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            emptyList()
        }
    }


    fun addBookInfo(bookRequest: BookInfoRequest): BookInfoResponse? {
        return try {
            bookInfoRepo.getBookInfo(bookRequest.bookTitle).firstOrNull().let {
                if (it == null) {
                    val newBook = BookInfo(
                        bookTitle = bookRequest.bookTitle,
                        bookAuthor = bookRequest.bookAuthor,
                        bookDescription = bookRequest.bookDescription,
                        bookIsbn = bookRequest.bookIsbn,
                        bookFormat = bookRequest.bookFormat,
                        bookLanguage = bookRequest.bookLanguage,
                        bookGenres = bookRequest.bookGenres,
                        bookEdition = bookRequest.bookEdition,
                        bookPublisher = bookRequest.bookPublisher,
                        bookPublishDate = bookRequest.bookPublishDate
                    )
                    bookInfoRepo.addBookInfo(newBook)?.let { b ->
                        return b.toBookInfoResponse()
                    }
                }
            }
            LogUtils.logInfo("${bookRequest.bookTitle}: book-already-existed")
            return null
        }
        catch (e: Exception) {
            LogUtils.logError(bookRequest.bookTitle + ": " + e.message.toString(), e)
            null
        }
    }


    fun deleteBookInfo(id: Long): BookInfoResponse? {
        return try {
            bookInfoRepo.getBookInfo("", id).firstOrNull().let {
                if (it != null) {
                    bookInfoRepo.deleteBookInfo(it.toBookInfo())?.let { b ->
                        return b.toBookInfoResponse()
                    }
                }
            }
            LogUtils.logInfo("$id: book-not-found")
            return null
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            null
        }
    }
    
}