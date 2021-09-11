package relativitization.universe.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class TString

@Serializable
data class IntString(val int: Int) : TString()

@Serializable
data class RealString(val str: String) : TString()

@Serializable
data class I18NString(
    val message: List<TString>,
    val arg: List<String>
) {
    fun toNormalString(): String = message.map {
        when (it) {
            is RealString -> it.str
            is IntString -> try {
                arg[it.int]
            } catch (e: Throwable) {
                ""
            }
        }
    }.reduce { acc, s ->  acc + s }
}