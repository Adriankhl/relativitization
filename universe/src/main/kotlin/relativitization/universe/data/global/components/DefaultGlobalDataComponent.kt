package relativitization.universe.data.global.components

import kotlinx.serialization.Serializable

@Serializable
sealed class DefaultGlobalDataComponent : GlobalDataComponent()

@Serializable
sealed class MutableDefaultGlobalDataComponent : MutableGlobalDataComponent()