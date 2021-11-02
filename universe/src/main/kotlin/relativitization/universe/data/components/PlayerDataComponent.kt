package relativitization.universe.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

sealed interface PlayerDataComponentClassWithName

@Serializable
sealed class PlayerDataComponent : PlayerDataComponentClassWithName

@Serializable
sealed class MutablePlayerDataComponent : PlayerDataComponentClassWithName

fun <T : PlayerDataComponentClassWithName> KClass<T>.name(): String = this.simpleName.toString()

fun PlayerDataComponent.name(): String = this::class.simpleName.toString()

fun MutablePlayerDataComponent.name(): String = this::class.simpleName.toString()


@Serializable
data class DataComponentMap(
    val dataMap: Map<String, PlayerDataComponent> = mapOf(),
) {
    constructor(dataList: List<PlayerDataComponent>) : this(
        dataMap = dataList.map {
            (it.name()) to it
        }.toMap()
    )

    internal inline fun <reified T : PlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: PlayerDataComponent? = dataMap[key.name()]
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

@Serializable
data class MutableDataComponentMap(
    val dataMap: MutableMap<String, MutablePlayerDataComponent> = mutableMapOf(),
) {
    constructor(dataList: List<MutablePlayerDataComponent>) : this(
        dataMap = dataList.map {
            // Drop first 7 character "Mutable"
            (it.name()).drop(7) to it
        }.toMap().toMutableMap()
    )

    internal inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent? = dataMap[(key.name()).drop(7)]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component in data map, use default value")
            defaultValue
        }
    }

    fun <T : MutablePlayerDataComponent> put(dataComponent: T) {
        dataMap[(dataComponent.name()).drop(7)] = dataComponent
    }

    fun <T : MutablePlayerDataComponent> remove(key: KClass<T>) {
        dataMap.remove((key.name()).drop(7))
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}