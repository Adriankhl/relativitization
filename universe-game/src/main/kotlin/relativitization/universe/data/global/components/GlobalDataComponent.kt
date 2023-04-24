package relativitization.universe.data.global.components

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class GlobalDataComponent

@Serializable
sealed class MutableGlobalDataComponent

/**
 * The key for the component in GlobalDataComponentMap
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun <T : GlobalDataComponent> KClass<T>.keyI(): String = this.serializer().descriptor.serialName

/**
 * The key for the component in MutableGlobalDataComponentMap
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun <T : MutableGlobalDataComponent> KClass<T>.keyM(): String = this.serializer().descriptor.serialName

/**
 * The key for the component in GlobalDataComponentMap
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun GlobalDataComponent.keyI(): String = this::class.serializer().descriptor.serialName

/**
 * The key for the component in MutableGlobalDataComponentMap
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
fun MutableGlobalDataComponent.keyM(): String = this::class.serializer().descriptor.serialName

@Serializable
data class GlobalDataComponentMap(
    val dataMap: Map<String, GlobalDataComponent> = mapOf(),
) {
    constructor(dataList: List<GlobalDataComponent>) : this(
        dataMap = dataList.associateBy { it.keyI() }
    )

    internal inline fun <reified T : GlobalDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: GlobalDataComponent = dataMap.getOrElse(defaultValue.keyI()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${defaultValue.keyI()} in global data map, use default value"
            )
            defaultValue
        }

        return if (data is T) {
            data
        } else {
            RelativitizationLogManager.getCommonLogger().error(
                "Global component ${data.keyI()} has incorrect type, use default value"
            )
            defaultValue
        }
    }

    inline fun <reified T : GlobalDataComponent> get(): T {
        val data: GlobalDataComponent = dataMap.getOrElse(T::class.keyI()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${T::class.keyI()} in global data map"
            )
            throw NoSuchElementException("No global data component ${T::class.keyI()}")
        }

        return if (data is T) {
            data
        } else {
            throw ClassCastException("Incorrect global data component type")
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
        dataMap = dataList.associateBy { it.keyM() }.toMutableMap()
    )

    internal inline fun <reified T : MutableGlobalDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: MutableGlobalDataComponent = dataMap.getOrElse(defaultValue.keyM()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find mutable global component ${defaultValue.keyM()} in data map, use default value"
            )
            defaultValue
        }

        return if (data is T) {
            data
        } else {
            RelativitizationLogManager.getCommonLogger().error(
                "Mutable global component ${data.keyM()} has incorrect type, use default value"
            )
            defaultValue
        }
    }

    inline fun <reified T : MutableGlobalDataComponent> get(): T {
        val data: MutableGlobalDataComponent = dataMap.getOrElse(T::class.keyM()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find mutable global component ${T::class.keyM()} in data map"
            )
            throw NoSuchElementException("No mutable global data component ${T::class.keyM()}")
        }

        return if (data is T) {
            data
        } else {
            throw ClassCastException("Incorrect mutable global data component type")
        }
    }

    fun <T : MutableGlobalDataComponent> put(dataComponent: T) {
        dataMap[dataComponent.keyM()] = dataComponent
    }


    inline fun <reified T : MutableGlobalDataComponent> remove() {
        dataMap.remove(T::class.keyM())
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}