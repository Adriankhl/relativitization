package relativitization.universe.data.global.components

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

sealed interface GlobalDataComponentCommon

@Serializable
sealed class GlobalDataComponent : GlobalDataComponentCommon

@Serializable
sealed class MutableGlobalDataComponent : GlobalDataComponentCommon

fun <T : GlobalDataComponentCommon> KClass<T>.name(): String = this.simpleName.toString()

fun GlobalDataComponent.name(): String = this::class.simpleName.toString()

fun MutableGlobalDataComponent.name(): String = this::class.simpleName.toString()
