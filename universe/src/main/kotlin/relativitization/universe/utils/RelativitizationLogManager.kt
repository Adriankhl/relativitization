package relativitization.universe.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.util.StackLocatorUtil

object RelativitizationLogManager {
    var useDefaultLoggerName: Boolean = false

    fun getLogger(): Logger = if (useDefaultLoggerName) {
        LogManager.getLogger("Default")
    } else {
        LogManager.getLogger(StackLocatorUtil.getCallerClass(2))
    }

    fun getLogger(name: String): Logger = LogManager.getLogger(name)
}