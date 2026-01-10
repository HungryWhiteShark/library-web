package com.example.app.base

import com.example.app.utils.LogUtils
import jakarta.persistence.Table
import org.springframework.context.ApplicationContext


abstract class Base {
    abstract fun context(): ApplicationContext

    fun <T> tableName(entity: Class<T>, isNative: Boolean = true): String {
        try {
            if (!isNative) return entity.simpleName
            return entity.getAnnotation(Table::class.java).name
        }
        catch (e: Exception) {
            logError(e, entity)
            return ""
        }
    }


    // By using reified, Kotlin handles the type casting under the hood, perfectly matching the return type.
    // T is constrained to be a non-nullable type.
    inline fun < reified T: Any> autoWired(clazz: Class<T>, nameBean: String?=null, base: Base?=null): T {
        nameBean?.let { it ->
            return context().getBean(it, clazz)
        }
        return try {
            context().getBean(clazz)
        }
        catch (_: Exception) {
            base?.let {
                return try {
                    clazz.getDeclaredConstructor(Any::class.java).newInstance(base)

                }
                catch (e: Exception) {
                    logError(e)
                    clazz.getDeclaredConstructor(Any::class.java).newInstance()
                }
            }
            try {
                clazz.getDeclaredConstructor().newInstance(null)
            }
            catch (_: Exception) {
                try {
                    clazz.getDeclaredConstructor(Any::class.java).newInstance(this)
                }
                catch (_: Exception) {
                    clazz.getDeclaredConstructor().newInstance()
                }
            }
        }
    }


    fun logError(api: Any, clazz: Any?=null) {
        autoWired(LogUtils::class.java).logError(api, clazz)
    }


    fun logError(e: Exception, clazz: Any? = null) {
        autoWired(LogUtils::class.java).logError(e, clazz)
    }


    fun logInfo(info: Any, clazz: Any?=null) {
        autoWired(LogUtils::class.java).logInfo(info, clazz)
    }

}




