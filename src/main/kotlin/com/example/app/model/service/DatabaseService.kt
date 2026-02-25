package com.example.app.model.service

import com.example.app.base.BaseService
import com.example.app.utils.LogUtils
import jakarta.persistence.PersistenceContext
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.hibernate.SessionFactory
import org.springframework.stereotype.Repository



@Repository
open class DatabaseService: BaseService() {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    fun<T> save (entity: T): T? {
        val session = entityManager.entityManagerFactory.unwrap(SessionFactory::class.java).openSession()
        return try {
            session.transaction.begin()
            val managedEntity = session.merge(entity)
            session.transaction.commit()
            managedEntity
        }
        catch (e: Exception) {
            session.transaction.rollback()
            LogUtils.logError(e.message.toString())
            null
        }
        finally {
            session.close()
        }
    }


    @Transactional
    fun<T> delete(entity: T): T? {
        return try {
            if (entityManager.contains(entity)) entityManager.remove(entity)
            else entityManager.remove(entityManager.merge(entity))
            entity
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString())
            null
        }
    }
}
