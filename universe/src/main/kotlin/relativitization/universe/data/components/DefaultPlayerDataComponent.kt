package relativitization.universe.data.components

import kotlinx.serialization.Serializable

@Serializable
sealed class DefaultPlayerDataComponent : PlayerDataComponent()

@Serializable
sealed class MutableDefaultPlayerDataComponent : MutablePlayerDataComponent()
