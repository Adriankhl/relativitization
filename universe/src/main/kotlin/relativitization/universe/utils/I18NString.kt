package relativitization.universe.utils

import kotlinx.serialization.Serializable
import kotlin.math.log

@Serializable
sealed class TString

@Serializable
data class IntString(val int: Int) : TString()

@Serializable
data class RealString(val str: String) : TString()

/**
 * Data format for translation
 *
 * @property message A list of string or int, int points to the string in arg
 * @property arg additional arguments for printing message
 */
@Serializable
data class I18NString(
    val message: List<TString>,
    val arg: List<String>
) {
    /**
     * Combine 2 I18NString, increase the index of IntString in the other I18NString
     */
    operator fun plus(other: I18NString): I18NString {
        val newMessage: List<TString> = message + other.message.map {
            when (it) {
                is RealString -> it
                is IntString -> IntString(it.int + arg.size)
            }
        }
        return I18NString(newMessage, arg + other.arg)
    }

    /**
     * Convert to a string
     */
    fun toNormalString(): String = message.map {
        when (it) {
            is RealString -> it.str
            is IntString -> try {
                arg[it.int]
            } catch (e: Throwable) {
                logger.error("Problematic I18NString: $this")
                ""
            }
        }
    }.reduce { acc, s ->  acc + s }

    /**
     * Convert to a list of string following the format of java's MessageFormat
     * Used in libgdx translation
     */
    fun toMessageFormat(): List<String> {
        val s1: String = message.map {
            when (it) {
                is RealString -> it.str
                is IntString -> try {
                    "{${it.int}}"
                } catch (e: Throwable) {
                    logger.error("Problematic I18NString: $this")
                    ""
                }
            }
        }.reduce { acc, s ->  acc + s }

        return listOf(s1) + arg
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}