package relativitization.universe.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.util.StackLocatorUtil

object RelativitizationLogManager {
    // Android does not support StackLocatorUtil.getCallerClass, use default logger instead
    private val useDefaultLoggerName: Boolean =
        System.getProperty("java.specification.vendor") == "The Android Project"

    fun getLogger(): RelativitizationLogger = if (useDefaultLoggerName) {
        RelativitizationLogger("DefaultLogger")
    } else {
        RelativitizationLogger()
    }

    fun getLogger(name: String): RelativitizationLogger = RelativitizationLogger(name)
}

class RelativitizationLogger(
    name: String = ""
) {
    private val logger: Logger = if (name.isBlank()) {
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

    fun info(message: String) {
        logger.info(message)
    }

    fun debug(message: String) {
        logger.debug(message)
    }

    fun trace(message: String) {
        logger.trace(message)
    }
}