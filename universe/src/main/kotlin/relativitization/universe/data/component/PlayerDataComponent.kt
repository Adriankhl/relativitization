package relativitization.universe.data.component

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class PlayerDataComponent

@Serializable
sealed class MutablePlayerDataComponent

@Serializable
data class DataComponentMap(
    val dataMap: Map<String, PlayerDataComponent> = mapOf(),
) {
    constructor(dataList: List<PlayerDataComponent>) : this(
        dataMap = dataList.map {
            (it::class.simpleName ?: "") to it
        }.toMap()
    )

    inline fun <reified T : PlayerDataComponent> getOrDefault(key: KClass<T>, defaultValue: T): T {
        val data: PlayerDataComponent? = dataMap[key.simpleName]
        return if(data is T) {
            data
        } else {
            defaultValue
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}