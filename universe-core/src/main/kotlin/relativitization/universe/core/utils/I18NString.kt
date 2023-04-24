package relativitization.universe.core.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class MessageString

@Serializable
data class NormalString(val str: String) : MessageString()

@Serializable
data class IntString(val int: Int) : MessageString()

@Serializable
data class IntTranslateString(val int: Int) : MessageString()

@Serializable
sealed class MessageVariableString

@Serializable
data class TranslateString(val str: String) : MessageVariableString()

@Serializable
data class NoTranslateString(val str: String) : MessageVariableString()

/**
 * The data of a message to be translated
 */
@Serializable
data class MessageFormatData(
    val template: String,
    val variableList: List<MessageVariableString>
)

/**
 * Data format for translation
 *
 * @property message A list of string or int, int points to the string in arg
 * @property arg additional arguments for printing message
 * @property next store next string, for storing a sequence of I18NString as a linked list
 */
@Serializable
data class I18NString(
    val message: List<MessageString>,
    val arg: List<String>,
    private var next: I18NString? = null
) {
    constructor(singleMessage: String, next: I18NString? = null) : this(
        message = listOf(NormalString(singleMessage)),
        arg = listOf(),
        next = next
    )

    /**
     * Convert to a list of string
     */
    fun toNormalStringList(): List<String> = listOf(message.map {
        when (it) {
            is NormalString -> it.str
            is IntString -> try {
                arg[it.int]
            } catch (e: Throwable) {
                logger.error("Problematic IntString in I18NString: $this")
                ""
            }
            is IntTranslateString -> try {
                arg[it.int]
            } catch (e: Throwable) {
                logger.error("Problematic IntTranslateString in I18NString: $this")
                ""
            }
        }
    }.reduce(String::plus)) + (next?.toNormalStringList() ?: listOf())

    /**
     * Convert to a strong
     */
    fun toNormalString(): String = toNormalStringList().reduce(String::plus)

    /**
     * Convert to a list of string following the format of java's MessageFormat
     * Used in libgdx translation
     */
    fun toMessageFormatList(): List<MessageFormatData> {
        val template: String = message.map {
            when (it) {
                is NormalString -> it.str
                is IntString -> "{${it.int}}"
                is IntTranslateString -> "{${it.int}}"
            }
        }.reduce(String::plus)

        val variableList: List<MessageVariableString> = message.filter {
            (it is IntString) || (it is IntTranslateString)
        }.map {
            when (it) {
                is IntString -> try {
                    NoTranslateString(arg[it.int])
                } catch (e: Throwable) {
                    logger.error("Problematic IntString in I18NString: $this")
                    NoTranslateString("")
                }
                is IntTranslateString -> try {
                    TranslateString(arg[it.int])
                } catch (e: Throwable) {
                    logger.error("Problematic IntString in I18NString: $this")
                    TranslateString("")
                }
                is NormalString -> throw Error("Wrong filter")
            }
        }

        return listOf(MessageFormatData(template, variableList)) +
                (next?.toMessageFormatList() ?: listOf())
    }

    fun last(): I18NString = next?.last() ?: this

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        fun combine(i18NStringList: List<I18NString>): I18NString = i18NStringList.foldRight(
            I18NString("")
        ) { i18NString, acc ->
            i18NString.last().next = acc
            i18NString
        }
    }
}