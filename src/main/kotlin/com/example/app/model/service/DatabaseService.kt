package com.example.app.model.service

import org.springframework.data.domain.PageRequest

interface DatabaseService {
    fun<T> isTable(entity: Class<T>): Boolean
    fun<T> tableName(entityDb: Class<T>, isNative: Boolean): String?
    fun<T> loadList(sql: String, clazzResult: Class<T>, params: ArrayList<Any> = ArrayList(),
                     paramList: HashMap<String, ArrayList<T>>, pageable: PageRequest?=null): List<T>

    fun<T> find(sql: String, clazzResult: Class<T>, params: ArrayList<Any> = ArrayList(),
                 paramList: HashMap<String, ArrayList<Any>> = hashMapOf()): T?

    fun<T> save(entity: T): T?

    fun<T> exists(sql: String, params: ArrayList<Any> = ArrayList(), paramList: HashMap<String, ArrayList<T>>
                  = hashMapOf(), pageable: PageRequest?=null): Boolean

    fun executeToUpdate(sql: String, params: ArrayList<Any> = ArrayList(), paramList: HashMap<String, ArrayList<Any>>
    = hashMapOf(), isNative: Boolean = true): Int
}
