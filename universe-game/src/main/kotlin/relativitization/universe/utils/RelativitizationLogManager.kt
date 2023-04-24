package relativitization.universe.utils

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.util.StackLocatorUtil

object RelativitizationLogManager {
    // Android does not support StackLocatorUtil.getCallerClass, use default logger instead
    private val useSimpleLogger: Boolean =
        System.getProperty("java.specification.vendor") == "The Android Project"

    private val simpleLogger = RelativitizationSimpleLogger(Level.OFF)

    private val commonLogger: RelativitizationLogger = if (useSimpleLogger) {
        simpleLogger
    } else {
        RelativitizationLog4j2Logger("CommonLogger")
    }

    fun getLogger(): RelativitizationLogger = if (useSimpleLogger) {
        simpleLogger
    } else {
        RelativitizationLog4j2Logger()
    }

    fun getCommonLogger(): RelativitizationLogger = commonLogger

    fun setSimpleLoggerLevel(level: Level) {
        simpleLogger.level = level
    }

    fun getLogger(name: String): RelativitizationLogger = if (useSimpleLogger) {
        simpleLogger
    } else {
        RelativitizationLog4j2Logger(name)
    }
}

abstract class RelativitizationLogger {
    abstract fun error(message: String)

    abstract fun warn(message: String)

    abstract fun info(message: String)

    abstract fun debug(message: String)

    abstract fun trace(message: String)
}

class RelativitizationLog4j2Logger(
    name: String = ""
) : RelativitizationLogger() {
    private val logger: Logger = if (name.isBlank()) {
        LogManager.getLogger(StackLocatorUtil.getCallerClass(4))
    } else {
        LogManager.getLogger(name)
    }

    override fun error(message: String) {
        logger.error(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun debug(message: String) {
        logger.debug(message)
    }

    override fun trace(message: String) {
        logger.trace(message)
    }
}

class RelativitizationSimpleLogger(
    var level: Level,
) : RelativitizationLogger() {
    override fun error(message: String) {
        if (level >= Level.ERROR) {
            println(message)
        }
    }

    override fun warn(message: String) {
        if (level >= Level.WARN) {
            println(message)
        }
    }

    override fun info(message: String) {
        if (level >= Level.INFO) {
            println(message)
        }
    }

    override fun debug(message: String) {
        if (level >= Level.DEBUG) {
            println(message)
        }
    }

    override fun trace(message: String) {
        if (level >= Level.TRACE) {
            println(message)
        }
    }
}