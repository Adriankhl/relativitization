package relativitization.universe.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

sealed class PlayerDataComponentCommon

@Serializable
sealed class PlayerDataComponent : PlayerDataComponentCommon()

@Serializable
sealed class MutablePlayerDataComponent : PlayerDataComponentCommon()

fun <T : PlayerDataComponentCommon> KClass<T>.name(): String = this.simpleName.toString()

fun PlayerDataComponent.name(): String = this::class.simpleName.toString()

fun MutablePlayerDataComponent.name(): String = this::class.simpleName.toString()


@Serializable
data class PlayerDataComponentMap(
    val dataMap: Map<String, PlayerDataComponent> = mapOf(),
) {
    constructor(dataList: List<PlayerDataComponent>) : this(
        dataMap = dataList.associateBy {
            (it.name())
        }
    )

    internal inline fun <reified T : PlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: PlayerDataComponent? = dataMap[key.name()]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component ${key.name()} in data map, use default value")
            defaultValue
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutablePlayerDataComponentMap(
    val dataMap: MutableMap<String, MutablePlayerDataComponent> = mutableMapOf(),
) {
    constructor(dataList: List<MutablePlayerDataComponent>) : this(
        dataMap = dataList.associateBy {
            // Drop first 7 character "Mutable"
            (it.name()).drop(7)
        }.toMutableMap()
    )

    internal inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent? = dataMap[(key.name()).drop(7)]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component ${key.name()} in data map, use default value")
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