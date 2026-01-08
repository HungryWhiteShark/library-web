package com.example.app.base

import com.example.app.ApplicationContextProvider
import org.springframework.context.ApplicationContext



open class BaseRepository(base: Any?): Base() {
    var context: ApplicationContext
    init {
        try {
            if (base == null) context = ApplicationContextProvider.applicationContext
            else {
                if (base is Base) context = base.context()
                else if (base is BaseController) context = base.context
                else {
                    context = base as? ApplicationContext ?: ApplicationContextProvider.applicationContext
                }
            }

        }
        catch (e: Exception) {
            context = ApplicationContextProvider.applicationContext
        }
    }


    override fun context(): ApplicationContext {
        return context()
    }
}
