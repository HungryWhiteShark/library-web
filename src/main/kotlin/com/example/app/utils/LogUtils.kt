package com.example.app.utils

import org.apache.logging.log4j.LogManager



object LogUtils {
    private val logger = LogManager.getLogger("GlobalLogger")

    fun logInfo(message: String, vararg params: Any) {
        logger.info(message, *params)
    }


    fun logError(message: String, t: Throwable? = null) {
        logger.error(message, t)
    }
}
