package relativitization.universe.utils

import kotlinx.serialization.Serializable

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
 * @property next store next string, for storing a sequence of I18NString as a linked list
 */
@Serializable
data class I18NString(
    val message: List<TString>,
    val arg: List<String>,
    val next: I18NString? = null
) {
    constructor(singleMessage: String, next: I18NString? = null) : this(
        message = listOf(RealString(singleMessage)),
        arg = listOf(),
        next = next
    )

    /**
     * Convert to a string
     */
    fun toNormalString(): List<String> = listOf(message.map {
        when (it) {
            is RealString -> it.str
            is IntString -> try {
                arg[it.int]
            } catch (e: Throwable) {
                logger.error("Problematic I18NString: $this")
                ""
            }
        }
    }.reduce(String::plus)) + (next?.toNormalString() ?: listOf())

    /**
     * Convert to a list of string following the format of java's MessageFormat
     * Used in libgdx translation
     */
    fun toMessageFormat(): List<List<String>> {
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

        return listOf(listOf(s1) + arg) + (next?.toMessageFormat() ?: listOf())
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        fun combine(i18NStringList: List<I18NString>): I18NString = i18NStringList.foldRight(
            I18NString("")
        ) { i18NString, acc ->
            i18NString.copy(next = acc)
        }
    }
}