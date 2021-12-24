package relativitization.universe.utils

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.message.EntryMessage
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.MessageFactory
import org.apache.logging.log4j.util.MessageSupplier
import org.apache.logging.log4j.util.Supplier

object RelativitizationLogManager {
    var isAndroid: Boolean = false

    fun getLogger(): Logger = if (isAndroid) {
        AndroidLogger
    } else {
        LogManager.getLogger()
    }

    fun getLogger(name: String): Logger = if (isAndroid) {
        AndroidLogger
    } else {
        LogManager.getLogger(name)
    }
}

/**
 * Empty Android logger
 * Log4j2 LogManager doesn't work for android
 */
object AndroidLogger : Logger {
    var showLog: Boolean = false

    override fun debug(message: String?) {
        if (showLog) {
            println(message)
        }
    }

    override fun error(message: String?) {
        if (showLog) {
            println(message)
        }
    }


    override fun info(message: String?) {
        if (showLog) {
            println(message)
        }
    }


    override fun catching(level: Level?, throwable: Throwable?) {
    }

    override fun catching(throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, message: Message?) {
    }

    override fun debug(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun debug(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, message: CharSequence?) {
    }

    override fun debug(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, message: Any?) {
    }

    override fun debug(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, message: String?) {
    }

    override fun debug(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun debug(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun debug(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun debug(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun debug(message: Message?) {
    }

    override fun debug(message: Message?, throwable: Throwable?) {
    }

    override fun debug(messageSupplier: MessageSupplier?) {
    }

    override fun debug(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun debug(message: CharSequence?) {
    }

    override fun debug(message: CharSequence?, throwable: Throwable?) {
    }

    override fun debug(message: Any?) {
    }

    override fun debug(message: Any?, throwable: Throwable?) {
    }


    override fun debug(message: String?, vararg params: Any?) {
    }

    override fun debug(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun debug(message: String?, throwable: Throwable?) {
    }

    override fun debug(messageSupplier: Supplier<*>?) {
    }

    override fun debug(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun debug(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun debug(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun debug(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun debug(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun debug(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun debug(
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
    ) {
    }

    override fun debug(
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
    ) {
    }

    override fun debug(
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
    ) {
    }

    override fun debug(message: String?, p0: Any?) {
    }

    override fun debug(message: String?, p0: Any?, p1: Any?) {
    }

    override fun debug(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun debug(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun debug(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun debug(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun debug(
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
    ) {
    }

    override fun debug(
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
    ) {
    }

    override fun entry() {
    }

    override fun entry(vararg params: Any?) {
    }

    override fun error(marker: Marker?, message: Message?) {
    }

    override fun error(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun error(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, message: CharSequence?) {
    }

    override fun error(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, message: Any?) {
    }

    override fun error(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, message: String?) {
    }

    override fun error(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun error(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun error(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun error(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun error(message: Message?) {
    }

    override fun error(message: Message?, throwable: Throwable?) {
    }

    override fun error(messageSupplier: MessageSupplier?) {
    }

    override fun error(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun error(message: CharSequence?) {
    }

    override fun error(message: CharSequence?, throwable: Throwable?) {
    }

    override fun error(message: Any?) {
    }

    override fun error(message: Any?, throwable: Throwable?) {
    }

    override fun error(message: String?, vararg params: Any?) {
    }

    override fun error(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun error(message: String?, throwable: Throwable?) {
    }

    override fun error(messageSupplier: Supplier<*>?) {
    }

    override fun error(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun error(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun error(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun error(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun error(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun error(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun error(
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
    ) {
    }

    override fun error(
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
    ) {
    }

    override fun error(
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
    ) {
    }

    override fun error(message: String?, p0: Any?) {
    }

    override fun error(message: String?, p0: Any?, p1: Any?) {
    }

    override fun error(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun error(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun error(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun error(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun error(
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
    ) {
    }

    override fun error(
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
    ) {
    }

    override fun exit() {
    }

    override fun <R : Any?> exit(result: R): R {
        return result
    }

    override fun fatal(marker: Marker?, message: Message?) {
    }

    override fun fatal(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun fatal(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, message: CharSequence?) {
    }

    override fun fatal(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, message: Any?) {
    }

    override fun fatal(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, message: String?) {
    }

    override fun fatal(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun fatal(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun fatal(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun fatal(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun fatal(message: Message?) {
    }

    override fun fatal(message: Message?, throwable: Throwable?) {
    }

    override fun fatal(messageSupplier: MessageSupplier?) {
    }

    override fun fatal(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun fatal(message: CharSequence?) {
    }

    override fun fatal(message: CharSequence?, throwable: Throwable?) {
    }

    override fun fatal(message: Any?) {
    }

    override fun fatal(message: Any?, throwable: Throwable?) {
    }

    override fun fatal(message: String?) {
    }

    override fun fatal(message: String?, vararg params: Any?) {
    }

    override fun fatal(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun fatal(message: String?, throwable: Throwable?) {
    }

    override fun fatal(messageSupplier: Supplier<*>?) {
    }

    override fun fatal(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun fatal(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun fatal(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun fatal(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun fatal(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun fatal(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun fatal(
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
    ) {
    }

    override fun fatal(
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
    ) {
    }

    override fun fatal(
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
    ) {
    }

    override fun fatal(message: String?, p0: Any?) {
    }

    override fun fatal(message: String?, p0: Any?, p1: Any?) {
    }

    override fun fatal(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun fatal(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun fatal(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun fatal(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun fatal(
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
    ) {
    }

    override fun fatal(
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
    ) {
    }

    override fun getLevel(): Level {
        return Level.OFF
    }

    override fun <MF : MessageFactory?> getMessageFactory(): MF? {
        return null
    }

    override fun getName(): String {
        return ""
    }

    override fun info(marker: Marker?, message: Message?) {
    }

    override fun info(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun info(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, message: CharSequence?) {
    }

    override fun info(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, message: Any?) {
    }

    override fun info(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, message: String?) {
    }

    override fun info(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun info(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun info(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun info(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun info(message: Message?) {
    }

    override fun info(message: Message?, throwable: Throwable?) {
    }

    override fun info(messageSupplier: MessageSupplier?) {
    }

    override fun info(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun info(message: CharSequence?) {
    }

    override fun info(message: CharSequence?, throwable: Throwable?) {
    }

    override fun info(message: Any?) {
    }

    override fun info(message: Any?, throwable: Throwable?) {
    }

    override fun info(message: String?, vararg params: Any?) {
    }

    override fun info(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun info(message: String?, throwable: Throwable?) {
    }

    override fun info(messageSupplier: Supplier<*>?) {
    }

    override fun info(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun info(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun info(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun info(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun info(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun info(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun info(
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
    ) {
    }

    override fun info(
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
    ) {
    }

    override fun info(
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
    ) {
    }

    override fun info(message: String?, p0: Any?) {
    }

    override fun info(message: String?, p0: Any?, p1: Any?) {
    }

    override fun info(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun info(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun info(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun info(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun info(
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
    ) {
    }

    override fun info(
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
    ) {
    }

    override fun isDebugEnabled(): Boolean {
        return false
    }

    override fun isDebugEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun isEnabled(level: Level?): Boolean {
        return false
    }

    override fun isEnabled(level: Level?, marker: Marker?): Boolean {
        return false
    }

    override fun isErrorEnabled(): Boolean {
        return false
    }

    override fun isErrorEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun isFatalEnabled(): Boolean {
        return false
    }

    override fun isFatalEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun isInfoEnabled(): Boolean {
        return false
    }

    override fun isInfoEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun isTraceEnabled(): Boolean {
        return false
    }

    override fun isTraceEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun isWarnEnabled(): Boolean {
        return false
    }

    override fun isWarnEnabled(marker: Marker?): Boolean {
        return false
    }

    override fun log(level: Level?, marker: Marker?, message: Message?) {
    }

    override fun log(level: Level?, marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun log(level: Level?, marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: MessageSupplier?,
        throwable: Throwable?
    ) {
    }

    override fun log(level: Level?, marker: Marker?, message: CharSequence?) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: CharSequence?,
        throwable: Throwable?
    ) {
    }

    override fun log(level: Level?, marker: Marker?, message: Any?) {
    }

    override fun log(level: Level?, marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun log(level: Level?, marker: Marker?, message: String?) {
    }

    override fun log(level: Level?, marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        vararg paramSuppliers: Supplier<*>?
    ) {
    }

    override fun log(level: Level?, marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun log(level: Level?, marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        messageSupplier: Supplier<*>?,
        throwable: Throwable?
    ) {
    }

    override fun log(level: Level?, message: Message?) {
    }

    override fun log(level: Level?, message: Message?, throwable: Throwable?) {
    }

    override fun log(level: Level?, messageSupplier: MessageSupplier?) {
    }

    override fun log(level: Level?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun log(level: Level?, message: CharSequence?) {
    }

    override fun log(level: Level?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun log(level: Level?, message: Any?) {
    }

    override fun log(level: Level?, message: Any?, throwable: Throwable?) {
    }

    override fun log(level: Level?, message: String?) {
    }

    override fun log(level: Level?, message: String?, vararg params: Any?) {
    }

    override fun log(level: Level?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun log(level: Level?, message: String?, throwable: Throwable?) {
    }

    override fun log(level: Level?, messageSupplier: Supplier<*>?) {
    }

    override fun log(level: Level?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun log(level: Level?, marker: Marker?, message: String?, p0: Any?) {
    }

    override fun log(level: Level?, marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?
    ) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?
    ) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun log(
        level: Level?,
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun log(
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
    ) {
    }

    override fun log(
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
    ) {
    }

    override fun log(
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
    ) {
    }

    override fun log(
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
    ) {
    }

    override fun log(level: Level?, message: String?, p0: Any?) {
    }

    override fun log(level: Level?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun log(level: Level?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun log(level: Level?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun log(
        level: Level?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun log(
        level: Level?,
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
    ) {
    }

    override fun log(
        level: Level?,
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
    ) {
    }

    override fun printf(level: Level?, marker: Marker?, format: String?, vararg params: Any?) {
    }

    override fun printf(level: Level?, format: String?, vararg params: Any?) {
    }

    override fun <T : Throwable?> throwing(level: Level?, throwable: T): T {
        return throwable
    }

    override fun <T : Throwable?> throwing(throwable: T): T {
        return throwable
    }

    override fun trace(marker: Marker?, message: Message?) {
    }

    override fun trace(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun trace(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, message: CharSequence?) {
    }

    override fun trace(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, message: Any?) {
    }

    override fun trace(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, message: String?) {
    }

    override fun trace(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun trace(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun trace(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun trace(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun trace(message: Message?) {
    }

    override fun trace(message: Message?, throwable: Throwable?) {
    }

    override fun trace(messageSupplier: MessageSupplier?) {
    }

    override fun trace(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun trace(message: CharSequence?) {
    }

    override fun trace(message: CharSequence?, throwable: Throwable?) {
    }

    override fun trace(message: Any?) {
    }

    override fun trace(message: Any?, throwable: Throwable?) {
    }

    override fun trace(message: String?) {
    }

    override fun trace(message: String?, vararg params: Any?) {
    }

    override fun trace(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun trace(message: String?, throwable: Throwable?) {
    }

    override fun trace(messageSupplier: Supplier<*>?) {
    }

    override fun trace(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun trace(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun trace(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun trace(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun trace(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun trace(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun trace(
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
    ) {
    }

    override fun trace(
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
    ) {
    }

    override fun trace(
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
    ) {
    }

    override fun trace(message: String?, p0: Any?) {
    }

    override fun trace(message: String?, p0: Any?, p1: Any?) {
    }

    override fun trace(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun trace(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun trace(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun trace(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun trace(
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
    ) {
    }

    override fun trace(
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
    ) {
    }

    override fun traceEntry(): EntryMessage? {
        return null
    }

    override fun traceEntry(format: String?, vararg params: Any?): EntryMessage? {
        return null
    }

    override fun traceEntry(vararg paramSuppliers: Supplier<*>?): EntryMessage? {
        return null
    }

    override fun traceEntry(format: String?, vararg paramSuppliers: Supplier<*>?): EntryMessage? {
        return null
    }

    override fun traceEntry(message: Message?): EntryMessage? {
        return null
    }

    override fun traceExit() {
    }

    override fun <R : Any?> traceExit(result: R): R {
        return result
    }

    override fun <R : Any?> traceExit(format: String?, result: R): R {
        return result
    }

    override fun traceExit(message: EntryMessage?) {
    }

    override fun <R : Any?> traceExit(message: EntryMessage?, result: R): R {
        return result
    }

    override fun <R : Any?> traceExit(message: Message?, result: R): R {
        return result
    }

    override fun warn(marker: Marker?, message: Message?) {
    }

    override fun warn(marker: Marker?, message: Message?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, messageSupplier: MessageSupplier?) {
    }

    override fun warn(marker: Marker?, messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, message: CharSequence?) {
    }

    override fun warn(marker: Marker?, message: CharSequence?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, message: Any?) {
    }

    override fun warn(marker: Marker?, message: Any?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, message: String?) {
    }

    override fun warn(marker: Marker?, message: String?, vararg params: Any?) {
    }

    override fun warn(marker: Marker?, message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun warn(marker: Marker?, message: String?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, messageSupplier: Supplier<*>?) {
    }

    override fun warn(marker: Marker?, messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun warn(message: Message?) {
    }

    override fun warn(message: Message?, throwable: Throwable?) {
    }

    override fun warn(messageSupplier: MessageSupplier?) {
    }

    override fun warn(messageSupplier: MessageSupplier?, throwable: Throwable?) {
    }

    override fun warn(message: CharSequence?) {
    }

    override fun warn(message: CharSequence?, throwable: Throwable?) {
    }

    override fun warn(message: Any?) {
    }

    override fun warn(message: Any?, throwable: Throwable?) {
    }

    override fun warn(message: String?) {
    }

    override fun warn(message: String?, vararg params: Any?) {
    }

    override fun warn(message: String?, vararg paramSuppliers: Supplier<*>?) {
    }

    override fun warn(message: String?, throwable: Throwable?) {
    }

    override fun warn(messageSupplier: Supplier<*>?) {
    }

    override fun warn(messageSupplier: Supplier<*>?, throwable: Throwable?) {
    }

    override fun warn(marker: Marker?, message: String?, p0: Any?) {
    }

    override fun warn(marker: Marker?, message: String?, p0: Any?, p1: Any?) {
    }

    override fun warn(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun warn(marker: Marker?, message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?
    ) {
    }

    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun warn(
        marker: Marker?,
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun warn(
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
    ) {
    }

    override fun warn(
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
    ) {
    }

    override fun warn(
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
    ) {
    }

    override fun warn(message: String?, p0: Any?) {
    }

    override fun warn(message: String?, p0: Any?, p1: Any?) {
    }

    override fun warn(message: String?, p0: Any?, p1: Any?, p2: Any?) {
    }

    override fun warn(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?) {
    }

    override fun warn(message: String?, p0: Any?, p1: Any?, p2: Any?, p3: Any?, p4: Any?) {
    }

    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?
    ) {
    }

    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?
    ) {
    }

    override fun warn(
        message: String?,
        p0: Any?,
        p1: Any?,
        p2: Any?,
        p3: Any?,
        p4: Any?,
        p5: Any?,
        p6: Any?,
        p7: Any?
    ) {
    }

    override fun warn(
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
    ) {
    }

    override fun warn(
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
    ) {
    }
}