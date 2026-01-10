package com.example.app.base

import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext

object ApplicationContextProvider {
    lateinit var applicationContext: ApplicationContext

    fun <T> autoWired(clazz: Class<T>, nameBean: String? = null): Any {
        nameBean?.let {
            return applicationContext.getBean(nameBean, clazz)
        }
        return applicationContext.getBean(clazz)
    }
}