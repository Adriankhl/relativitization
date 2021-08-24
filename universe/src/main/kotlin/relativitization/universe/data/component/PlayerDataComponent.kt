package relativitization.universe.data.component

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class PlayerDataComponent

@Serializable
sealed class MutablePlayerDataComponent

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class DataComponentMap(
    val dataMap: Map<String, PlayerDataComponent> = mapOf(),
) {
    constructor(dataList: List<PlayerDataComponent>) : this(
        dataMap = dataList.map {
            it::class.serializer().descriptor.serialName to it
        }.toMap()
    )

    internal inline fun <reified T : PlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: PlayerDataComponent? = dataMap[key.serializer().descriptor.serialName]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component in data map, use default value")
            defaultValue
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class MutableDataComponentMap(
    val dataMap: MutableMap<String, MutablePlayerDataComponent> = mutableMapOf(),
) {
    constructor(dataList: List<MutablePlayerDataComponent>) : this(
        dataMap = dataList.map {
            it::class.serializer().descriptor.serialName to it
        }.toMap().toMutableMap()
    )

    internal inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent? = dataMap[key.serializer().descriptor.serialName]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component in data map, use default value")
            defaultValue
        }
    }

    fun <T : MutablePlayerDataComponent> put(dataComponent: T) {
        dataMap[dataComponent::class.serializer().descriptor.serialName] = dataComponent
    }

    fun <T : MutablePlayerDataComponent> remove(key: KClass<T>) {
        dataMap.remove(key::class.serializer().descriptor.serialName)
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}