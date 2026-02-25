package com.example.app.model.service

import com.example.app.base.BaseService
import com.example.app.utils.LogUtils
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.stereotype.Service



@Service
class DeviceInfoService: BaseService() {
    private val uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().build()

    fun getDeviceInfo(userAgent: String): Result {
        return try {
            val agent = uaa.parse(userAgent)
            val os = agent.getValue("OperatingSystemNameVersion")
            val browser = agent.getValue("AgentNameVersion")
            LogUtils.logInfo("$browser on $os")
            Result("$browser on $os", 100)
        }
        catch (e: Exception) {
            LogUtils.logError(e.message.toString(), e)
            Result(e.message.toString(), 101)
        }
    }
}
