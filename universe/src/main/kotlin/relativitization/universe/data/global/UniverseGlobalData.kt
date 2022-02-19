package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.global.components.*
import kotlin.reflect.full.createInstance

@Serializable
data class UniverseGlobalData(
    val globalDataComponentMap: GlobalDataComponentMap = GlobalDataComponentMap(
        DefaultGlobalDataComponent::class.sealedSubclasses.map { it.createInstance() },
    )
)

@Serializable
data class MutableUniverseGlobalData(
    var globalDataComponentMap: MutableGlobalDataComponentMap = MutableGlobalDataComponentMap(
        MutableDefaultGlobalDataComponent::class.sealedSubclasses.map { it.createInstance() },
    )
)