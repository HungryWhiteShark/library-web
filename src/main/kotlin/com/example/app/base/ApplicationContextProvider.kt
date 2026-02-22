package com.example.app.base

import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContextAware



@Component
class ApplicationContextInjector: ApplicationContextAware {
    override fun setApplicationContext(context: ApplicationContext) {
        ApplicationContextProvider.applicationContext = context
    }
}



object ApplicationContextProvider {
    lateinit var applicationContext: ApplicationContext

    fun <T> autoWired(clazz: Class<T>): T {
        checkInitialization()
        return applicationContext.getBean(clazz)
    }


    private fun checkInitialization() {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException(
                "ApplicationContextProvider is not initialized. " +
                        "Ensure ApplicationContextInjector is annotated with @Component."
            )
        }
    }


    fun <T> autoWired(clazz: Class<T>, nameBean: String? = null): Any {
        checkInitialization()
        nameBean?.let {
            return applicationContext.getBean(nameBean, clazz)
        }
        return applicationContext.getBean(clazz)
    }
}