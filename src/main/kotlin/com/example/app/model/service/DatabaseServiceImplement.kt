package com.example.app.model.service

import com.example.app.base.BaseService
import com.example.app.utils.toListObject
import jakarta.persistence.PersistenceContext
import com.example.app.utils.toNodeJson
import com.example.app.utils.toObject
import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import jakarta.transaction.Transactional
import org.hibernate.SessionFactory
import org.hibernate.query.NativeQuery
import org.hibernate.query.Query
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service


@Service
@Transactional
@Repository
class DatabaseServiceImplement: DatabaseService, BaseService() {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun <T> isTable(entity: Class<T>): Boolean {
        return try {
            entity.getAnnotation(Table::class.java).name.isNotBlank()
        }
        catch (_: Exception) { false }
    }


    override fun <T> loadList(sql: String, clazzResult: Class<T>, params: ArrayList<Any>,
                              paramList: HashMap<String, ArrayList<T>>, pageable: PageRequest?): List<T> {
        return try {
            val query = if (isTable(clazzResult))
                entityManager.createNativeQuery(sql, clazzResult) else entityManager.createNativeQuery(sql)
            params.forEachIndexed { index, any -> query.setParameter(index, any)}
            paramList.forEach { (t, u) -> query.setParameter(t, u) }

            pageable?.let {
                query.firstResult = (it.pageNumber) * it.pageSize
                query.setMaxResults(it.pageSize)
            }

            if (!isTable(clazzResult)) {
                // setTupleTransformer: Has access to the aliases (column names).
                // need these to be the keys in your Map.
                // setTupleTransformer turns an Object[] into a UserDto (row level)
                query.unwrap(Query::class.java).setTupleTransformer { tuple, alias ->
                    alias.indices.associate { i -> alias[i] to tuple[i] }
                } as NativeQuery<Map<String, Any?>>
            }

            if (isTable(clazzResult)) return query.resultList as List<T>

            query.resultList.toNodeJson().toListObject(clazzResult)
        }

        catch (_: Exception) {
            logError(sql, this)
            emptyList()
        }
    }


    override fun <T> find(sql: String, clazzResult: Class<T>,
        params: ArrayList<Any>, paramList: HashMap<String, ArrayList<Any>>): T? {
        return try {
            val query = if (isTable(clazzResult))
                entityManager.createNativeQuery(sql, clazzResult)

            else entityManager.createNativeQuery(sql)
            params.forEachIndexed { index, any -> query.setParameter(index, any)}
            paramList.forEach { (t, u) -> query.setParameter(t, u) }
            if (!isTable(clazzResult)) {
                // setTupleTransformer: Has access to the aliases (column names).
                // need these to be the keys in your Map.
                // setTupleTransformer turns an Object[] into a UserDto (row level)
                query.unwrap(Query::class.java).setTupleTransformer { tuple, alias ->
                    alias.indices.associate { i -> alias[i] to tuple[i] }
                } as NativeQuery<Map<String, Any?>>
            }
            val data = query.resultList

            if (data.isNotEmpty()) {
                if (isTable(clazzResult)) {
                    return data[0] as T
                }
                data[0]?.let {
                    return it.toNodeJson().toObject(clazzResult)
                }
            }
            else null

        }
        catch (e: Exception) {
            logError(e, this)
            null
        }
    }

    @Transactional
    override fun<T> save (entity: T): T? {
        val session = entityManager.entityManagerFactory.unwrap(SessionFactory::class.java).openSession()
        return try {
            session.transaction.begin()
            val managedEntity = session.merge(entity)
            session.transaction.commit()
            managedEntity
        }
        catch (e: Exception) {
            session.transaction.rollback()
            logError(e)
            null
        }
        finally {
            session.close()
        }
    }


    @Transactional
    override fun<T> delete(entity: T): T? {
        return try {
            if (entityManager.contains(entity)) entityManager.remove(entity)
            else entityManager.remove(entityManager.merge(entity))
            entity
        }
        catch (e: Exception) {
            logError(e)
            null
        }
    }


    override fun <T> exists(sql: String, params: ArrayList<Any>, paramList: HashMap<String, ArrayList<T>>,
        pageable: PageRequest?): Boolean {

        return try {
            val query = entityManager.createNativeQuery(sql)
            params.forEachIndexed { index, any -> query.setParameter(index, any) }

            paramList.forEach { (t, u) -> query.setParameter(t, u) }

            pageable?.let {
                query.firstResult = (it.pageNumber) * it.pageSize
                query.setMaxResults(it.pageSize)
            }

            query.unwrap(Query::class.java).setTupleTransformer { tuple, alias ->
                alias.indices.associate { i -> alias[i] to tuple[i] }
            } as NativeQuery<Map<String, Any?>>


            return query.resultList.isNotEmpty()
        }
        catch (e: Exception) {
            logError(e)
            false
        }
    }


    @Transactional
    override fun executeToUpdate(sql: String, params: ArrayList<Any>,
        paramList: HashMap<String, ArrayList<Any>>, isNative: Boolean): Int {

        val session = entityManager.entityManagerFactory.unwrap(SessionFactory::class.java).openSession()
        return try {
            session.transaction.begin()
            val query = if (isNative) entityManager.createNativeQuery(sql) else
                entityManager.createQuery(sql)

            params.forEachIndexed { index, any -> query.setParameter(index, any) }

            paramList.forEach { (t, u) -> query.setParameter(t, u) }
            query.executeUpdate()
        }
        catch (e: Exception) {
            logError(e)
            -1
        }
        finally {
            session.transaction.commit()
            session.close()
        }
    }


//    override fun <T> tableName(entityDb: Class<T>, isNative: Boolean): String {
//        try {
//            if (!isNative) return entityDb.simpleName
//            return entityDb.getAnnotation(Table::class.java).name
//
//        }
//        catch (e: Exception) {
//            logError(e)
//            return ""
//        }
//    }
}

