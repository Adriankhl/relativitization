package relativitization.universe.data.global.components

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

sealed class GlobalDataComponentCommon

@Serializable
sealed class GlobalDataComponent : GlobalDataComponentCommon()

@Serializable
sealed class MutableGlobalDataComponent : GlobalDataComponentCommon()

fun <T : GlobalDataComponentCommon> KClass<T>.name(): String = this.simpleName.toString()

fun GlobalDataComponent.name(): String = this::class.simpleName.toString()

fun MutableGlobalDataComponent.name(): String = this::class.simpleName.toString()

@Serializable
data class GlobalDataComponentMap(
    val dataMap: Map<String, GlobalDataComponent> = mapOf(),
) {
    constructor(dataList: List<GlobalDataComponent>) : this(
        dataMap = dataList.associateBy {
            (it.name())
        }
    )

    internal inline fun <reified T : GlobalDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: GlobalDataComponent? = dataMap[key.name()]
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
data class MutableGlobalDataComponentMap(
    val dataMap: MutableMap<String, MutableGlobalDataComponent> = mutableMapOf(),
) {
    constructor(dataList: List<MutableGlobalDataComponent>) : this(
        dataMap = dataList.associateBy {
            // Drop first 7 character "Mutable"
            (it.name()).drop(7)
        }.toMutableMap()
    )

    internal inline fun <reified T : MutableGlobalDataComponent> getOrDefault(
        key: KClass<T>,
        defaultValue: T
    ): T {
        val data: MutableGlobalDataComponent? = dataMap[(key.name()).drop(7)]
        return if (data is T) {
            data
        } else {
            logger.error("Cannot find component ${key.name()} in data map, use default value")
            defaultValue
        }
    }

    fun <T : MutableGlobalDataComponent> put(dataComponent: T) {
        dataMap[(dataComponent.name()).drop(7)] = dataComponent
    }

    fun <T : MutableGlobalDataComponent> remove(key: KClass<T>) {
        dataMap.remove((key.name()).drop(7))
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}