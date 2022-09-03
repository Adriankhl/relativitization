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

    inline fun <reified T : PlayerDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: PlayerDataComponent = dataMap.getOrElse(defaultValue.name()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${defaultValue.name()} in data map, use default value"
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
        val data: PlayerDataComponent = dataMap.getOrElse(T::class.name()) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${T::class.name()} in data map"
            )
            throw NoSuchElementException("No data component ${T::class.name()}")
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
        dataMap = dataList.associateBy {
            // Drop first 7 character "Mutable"
            (it.name()).drop(7)
        }.toMutableMap()
    )

    inline fun <reified T : MutablePlayerDataComponent> getOrDefault(
        defaultValue: T
    ): T {
        val data: MutablePlayerDataComponent = dataMap.getOrElse(defaultValue.name().drop(7)) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find component ${defaultValue.name()} in data map, use default value"
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
        val data: MutablePlayerDataComponent = dataMap.getOrElse(T::class.name().drop(7)) {
            RelativitizationLogManager.getCommonLogger().error(
                "Cannot find mutable component ${T::class.name()} in data map"
            )
            throw NoSuchElementException("No mutable data component ${T::class.name()}")
        }

        return if (data is T) {
            data
        } else {
            throw ClassCastException("Incorrect mutable data component type")
        }
    }

    fun <T : MutablePlayerDataComponent> put(dataComponent: T) {
        dataMap[(dataComponent.name()).drop(7)] = dataComponent
    }

    inline fun <reified T : MutablePlayerDataComponent> remove() {
        dataMap.remove((T::class.name()).drop(7))
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}