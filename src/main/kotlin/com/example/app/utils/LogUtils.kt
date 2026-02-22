package com.example.app.utils

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.tomcat.util.threads.ThreadPoolExecutor
import tools.jackson.databind.ObjectMapper
import java.io.PrintWriter
import java.io.StringWriter
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter



enum class LogType(val typeName: String) {
    INFO("info"),
    ERROR("error")
}


class LogUtils(var serviceName: String = "") {
    val errorLog: Log = LogFactory.getLog("error")
    private val mapper = ObjectMapper()
    val poolLogger = ThreadPoolExecutor(
        4,4,0L, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue<Runnable>(10000), // queue size = 10k log
        ThreadPoolExecutor.CallerRunsPolicy()
    )
    private val loggers: Log = LogFactory.getLog("info")

    fun Any.safeToJson(mapper: ObjectMapper): String {
        return try {
            mapper.writeValueAsString(this)
        }
        catch (_: Exception) {
            this.toString()
        }

    }


    inline fun <reified T> logError(e: Exception) {
        try {
            val clazz = T::class.java.simpleName
            val errors = StringWriter().also {
                e.printStackTrace(PrintWriter(it)).toString()
            }

            errorLog.error("class::${clazz} - $errors")
            val data = LogEntity(serviceName, LogType.ERROR).apply {
                logTime = System.currentTimeMillis()
                dataLog = errors
            }
            poolLogger.execute(LoggerRunnable(data))
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun logError(error: Any, clazz: Any?=null) {
        try {
            errorLog.error("class::${clazz?.javaClass?.simpleName}-${error.safeToJson(mapper)}")
            val data = LogEntity(serviceName, LogType.ERROR).apply {
                logTime = System.currentTimeMillis()
                dataLog = error
            }
            poolLogger.execute(LoggerRunnable(data))

        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun logInfo(info: Any, clazz: Any? = null) {
        try {
            loggers.info("class::${clazz?.javaClass?.simpleName} - $info")
            val data = LogEntity(serviceName, LogType.INFO).apply {
                logTime = System.currentTimeMillis()
                dataLog = info

            }
            poolLogger.execute(LoggerRunnable(data))
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }


    inner class LoggerRunnable(var data: LogEntity): Runnable {
        override fun run() {
            data.uuid = UUID.randomUUID().toString()
            data.logTime = System.currentTimeMillis()
            val instant = Instant.ofEpochMilli(data.logTime)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            data.timeView = instant.atZone(ZoneId.of("+07:00")).format(formatter)
        }
    }

}


class LogEntity (
    @JsonProperty("serviceName") var serviceName: String,
    @JsonProperty("logType") var logType: LogType = LogType.INFO
) {
    @JsonProperty("logTime")
    var logTime: Long = 0

    @JsonProperty("dataLog")
    var dataLog: Any = Any()

    @JsonProperty("timeView")
    var timeView: String = ""

    var uuid: String? = null
}

