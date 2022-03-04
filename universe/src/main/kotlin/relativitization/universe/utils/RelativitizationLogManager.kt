package relativitization.universe.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.util.StackLocatorUtil

object RelativitizationLogManager {
    // Android does not support StackLocatorUtil.getCallerClass, use default logger instead
    var useDefaultLoggerName: Boolean = false

    fun getLogger(): RelativitizationLogger = if (useDefaultLoggerName) {
        RelativitizationLogger("Default")
    } else {
        RelativitizationLogger()
    }

    fun getLogger(name: String): RelativitizationLogger = RelativitizationLogger(name)
}

class RelativitizationLogger(
    name: String = ""
) {
    val logger: Logger = if (name.isBlank()) {
        LogManager.getLogger(StackLocatorUtil.getCallerClass(4))
    } else {
        LogManager.getLogger(name)
    }

    fun error(message: String) {
        logger.error(message)
    }

    fun warn(message: String) {
        logger.warn(message)
    }

    fun debug(message: String) {
        logger.debug(message)
    }

    fun info(message: String) {
        logger.info(message)
    }
}