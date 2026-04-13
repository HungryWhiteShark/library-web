package com.example.app.db

import jakarta.persistence.*
import jakarta.persistence.GenerationType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime



@Entity
@Table(
    name = BookInfo.TABLE,
    indexes = [
        Index(name = "idx_${BookInfo.TABLE}_bookId", columnList = "bookId")
    ]
)
data class BookInfo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var bookId: Long = 0L,

    @Column(columnDefinition = "text", nullable = false)
    var bookTitle: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var bookAuthor: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var bookDescription: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var bookLanguage: String = "",

    @Column(columnDefinition = "text", nullable = true)
    var bookIsbn: String? = null,

    @Column(columnDefinition = "text", nullable = false)
    var bookGenres: String = "",

    @Column(columnDefinition = "text", nullable = false)
    var bookFormat: String = "",

    @Column(columnDefinition = "text", nullable = true)
    var bookEdition: String? = null,

    @Column(columnDefinition = "text", nullable = true)
    var bookPublisher: String? = null,

    @Column(columnDefinition = "text", nullable = true)
    var bookPublishDate: String? = null,

    @CreationTimestamp
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "timestamp", nullable = true)
    var deleted: LocalDateTime = LocalDateTime.now()

): Serializable {
    companion object {
        const val TABLE = "book_info"
    }
}
