package relativitization.universe.utils

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.spi.AbstractLogger

object RelativitizationLogManager {
    var isAndroid: Boolean = false

    inline fun getLogger(): Logger = if (isAndroid) {
        AndroidLogger
    } else {
        LogManager.getLogger()
    }
}

object AndroidLogger : AbstractLogger() {
    override fun getLevel(): Level {
        return Level.OFF
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: Message?,
        t: Throwable?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: CharSequence?,
        t: Throwable?
    ): Boolean {
        return false
    }

    override fun isEnabled(level: Level?, marker: Marker?, message: Any?, t: Throwable?): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        t: Throwable?
    ): Boolean {
        return false
    }

    override fun isEnabled(level: Level?, marker: Marker?, message: String?): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        vararg params: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(level: Level?, marker: Marker?, message: String?, p0: Any?): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?
    ): Boolean {
        return false
    }

    override fun isEnabled(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?,
        p8: Any?,
        p9: Any?
    ): Boolean {
        return false
    }

    override fun logMessage(
        fqcn: String?,
        level: Level?,
        marker: Marker?,
        message: Message?,
        t: Throwable?
    ) {
    }

}