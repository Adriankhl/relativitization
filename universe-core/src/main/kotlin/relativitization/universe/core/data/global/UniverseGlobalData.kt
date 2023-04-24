package relativitization.universe.core.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.global.components.GlobalDataComponentMap
import relativitization.universe.core.data.global.components.MutableGlobalDataComponentMap

@Serializable
data class UniverseGlobalData(
    val globalDataComponentMap: GlobalDataComponentMap = GlobalDataComponentMap()
)

@Serializable
data class MutableUniverseGlobalData(
    var globalDataComponentMap: MutableGlobalDataComponentMap = MutableGlobalDataComponentMap()
)