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

    inline fun <reified T : PlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: PlayerDataComponent? = dataMap[key.simpleName]
        return if (data is T) {
            data
        } else {
            defaultValue
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutableDataComponentMap(
    val dataMap: MutableMap<String, MutablePlayerDataComponent> = mutableMapOf(),
) {
    constructor(dataList: List<MutablePlayerDataComponent>) : this(
        dataMap = dataList.map {
            (it::class.simpleName ?: "") to it
        }.toMap().toMutableMap()
    )

    inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent? = dataMap[key.simpleName]
        return if (data is T) {
            data
        } else {
            defaultValue
        }
    }

    fun <T : MutablePlayerDataComponent> put(dataComponent: T) {
        dataMap[(dataComponent::class.simpleName ?: "")] = dataComponent
    }

    fun <T : MutablePlayerDataComponent> remove(key: KClass<T>) {
        dataMap.remove(key::class.simpleName)
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}