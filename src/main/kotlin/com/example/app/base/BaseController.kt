package com.example.app.base

import com.example.app.base.ApplicationContextProvider
import com.example.app.config.JwtUtil
import com.example.app.utils.LogUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import java.util.ResourceBundle



@CrossOrigin(value = ["*"])
open class BaseController: ResponseEntityExceptionHandler() {
    @Autowired
    lateinit var logUtils: LogUtils

    @Autowired
    lateinit var context: ApplicationContext

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var request: HttpServletRequest

    lateinit var userLanguage: String


    private fun getDefaultLocale(name: String = "messages"): ResourceBundle {
        return ResourceBundle.getBundle("i18n/${name}", Locale("vi", "VN"))
    }


    open fun getLocale(name: String = "messages"): ResourceBundle {
        if (userLanguage == "en") return ResourceBundle.getBundle("i18n/${name}", Locale.ENGLISH)
        return getDefaultLocale(name)
    }


    fun handleMessageKey(messageKey: String, fileMsgName: String = "messages"): String {
        var msg = messageKey
        try {
            msg = getLocale(fileMsgName).getString(msg)

        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return msg
    }


    protected fun response(code: Int = 100, message: String = "success"): ResponseEntity<Any> {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
            BaseResponse(code, message)
        )
    }


    protected fun response(response: BaseResponse): ResponseEntity<*> {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response)
    }


    protected fun<T> responseData(data: T?, code: Int = 100, message: String = "success"): ResponseEntity<Any> {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
            BaseResponse.Data(data, code, message)
        )
    }


    protected fun<T> responseDataList(list: List<T>, code: Int = 100, message: String = "success"): ResponseEntity<Any> {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
            BaseResponse.DataList(list, code, message)
        )
    }


    fun loadHeader(key: String): String? {
        try {
            val attr = RequestContextHolder.getRequestAttributes()
            attr?.let {
                val req = (attr as ServletRequestAttributes).request
                return req.getHeader(key)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun userIdRequest(): String {
        val token = jwtUtil.getAllClaimsFromToken(loadHeader("token"))?: return "0"
        return token["userId"].toString()
    }


    inline fun <reified T: Any> autoWired(clazz: Class<T>, nameBean: String? = null,
                                           initArgs: Array<Any>? = arrayOf()): T {
        try {
            nameBean?.let {
                return context.getBean(nameBean, clazz)
            }
            return try {
                try {
                    ApplicationContextProvider.autoWired(clazz)
                } catch (_: Exception) {
                    if (initArgs.isNullOrEmpty()) clazz.getDeclaredConstructor().newInstance()
                    else clazz.getDeclaredConstructor(clazz).newInstance()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                clazz.getDeclaredConstructor(clazz).newInstance()
            } as T
        }
        catch (e: Exception) {
            e.printStackTrace()
            return try {
                clazz.getDeclaredConstructor(clazz).newInstance(null)
            }
            catch (e: Exception) {
                e.printStackTrace()
                return if (initArgs.isNullOrEmpty()) {
                    clazz.getDeclaredConstructor().newInstance()
                }
                else clazz.getDeclaredConstructor().newInstance(initArgs)

            }
        }
    }


    open fun deleteTempFile(file: File, inputStream: FileInputStream? = null, folder: Boolean = false) {
        Thread {
            try {
                Thread.sleep(1000)
                if (folder) {
                    for (item in file.listFiles()!!) {
                        if (!item.delete())
                            logUtils.logInfo("Cannot delete file: ${file.absolutePath}")
                    }
                }
                inputStream?.close()
                logUtils.logInfo("Delete file: ${file.delete()}")
            }
            catch (e: Exception) {
                logUtils.logError(e, this)
            }
        }
    }
}
