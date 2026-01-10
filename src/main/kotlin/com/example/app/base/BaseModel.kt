package com.example.app.base

import com.example.app.base.ApplicationContextProvider
import org.springframework.context.ApplicationContext

open class BaseModel(base: Any?): Base() {
    protected lateinit var context: ApplicationContext
    init {
        try {
            base?.let {
                if (base is Base) context = base.context()
                if (base is BaseController) {
                    context = base.context
                    return@let
                }
                context = base as? ApplicationContext ?: ApplicationContextProvider.applicationContext
            }
            if (base == null) context = ApplicationContextProvider.applicationContext

        }
        catch (e: Exception) {
            logError(e)
        }
    }


    override fun context(): ApplicationContext {
        return context
    }
}
