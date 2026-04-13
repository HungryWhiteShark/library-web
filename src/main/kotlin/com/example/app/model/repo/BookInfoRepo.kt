package com.example.app.model.repo

import com.example.app.db.BookInfo
import com.example.app.model.dto.BookInfoResponse
import com.example.app.model.service.DatabaseService
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime



@Repository
@Transactional
class BookInfoRepo(private val jdbc: NamedParameterJdbcTemplate, private val db: DatabaseService) {

    fun getBookInfo(title: String, id: Long? = null): List<BookInfoResponse> {
        val params = hashMapOf<String, Any>()
        val sql = buildString {}

        return jdbc.query(
            sql, params,
            BeanPropertyRowMapper(BookInfoResponse::class.java)
        )
    }


    fun addBookInfo(data: BookInfo): BookInfo? = db.save(data)


    fun deleteBookInfo(data: BookInfo): BookInfo? {
        data.deleted = LocalDateTime.now()
        return db.save(data)

    }

}
