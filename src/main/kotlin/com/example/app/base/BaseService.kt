package com.example.app.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext



open class BaseService: Base() {
    @Autowired
    lateinit var context: ApplicationContext
    override fun context(): ApplicationContext {
        return context
    }
}

