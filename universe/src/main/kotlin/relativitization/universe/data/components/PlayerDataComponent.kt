package relativitization.universe.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class PlayerDataComponent

@Serializable
sealed class MutablePlayerDataComponent

/**
 * The key for the component in PlayerDataComponentMap
 */
fun <T : PlayerDataComponent> KClass<T>.keyI(): String = this.simpleName.toString()

/**
 * The key for the component in MutablePlayerDataComponentMap, the first 7 characters
 * should be "Mutable", they are dropped to match keyI()
 */
fun <T : MutablePlayerDataComponent> KClass<T>.keyM(): String = this.simpleName.toString().drop(7)

/**
 * The key for the component in PlayerDataComponentMap
 */
fun PlayerDataComponent.keyI(): String = this::class.simpleName.toString()

/**
 * The key for the component in MutablePlayerDataComponentMap, the first 7 characters
 * should be "Mutable", they are dropped to match keyI()
 */
fun MutablePlayerDataComponent.keyM(): String = this::class.simpleName.toString().drop(7)


@Serializable
data class PlayerDataComponentMap(
    val dataMap: Map<String, PlayerDataComponent> = mapOf(),
) {
    constructor(dataList: List<PlayerDataComponent>) : this(
        dataMap = dataList.associateBy { it.keyI() }
    )

    inline fun <reified T : PlayerDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: PlayerDataComponent = dataMap.getOrElse(defaultValue.keyI()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${defaultValue.keyI()} in data map, use default value"
            )
            defaultValue
        }

        return if (data is T) {
            data
        } else {
            defaultValue
        }
    }

    inline fun <reified T : PlayerDataComponent> get(): T {
        val data: PlayerDataComponent = dataMap.getOrElse(T::class.keyI()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${T::class.keyI()} in data map"
            )
            throw NoSuchElementException("No data component ${T::class.keyI()}")
        }

        return if (data is T) {
            data
        } else {
            throw ClassCastException("Incorrect data component type")
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
        dataMap = dataList.associateBy { it.keyM() }.toMutableMap()
    )

    inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent = dataMap.getOrElse(defaultValue.keyM()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find mutable component ${defaultValue.keyM()} in data map, use default value"
            )
            defaultValue
        }

        return if (data is T) {
            data
        } else {
            defaultValue
        }
    }

    inline fun <reified T : MutablePlayerDataComponent> get(): T {
        val data: MutablePlayerDataComponent = dataMap.getOrElse(T::class.keyM()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find mutable component ${T::class.keyM()} in data map"
            )
            throw NoSuchElementException("No mutable data component ${T::class.keyM()}")
        }

        return if (data is T) {
            data
        } else {
            throw ClassCastException("Incorrect mutable data component type")
        }
    }

    fun <T : MutablePlayerDataComponent> put(dataComponent: T) {
        dataMap[dataComponent.keyM()] = dataComponent
    }

    inline fun <reified T : MutablePlayerDataComponent> remove() {
        dataMap.remove(T::class.keyM())
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}