package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.global.components.GlobalDataComponentMap
import relativitization.universe.data.global.components.MutableGlobalDataComponentMap

@Serializable
data class UniverseGlobalData(
    val globalDataComponentMap: GlobalDataComponentMap = GlobalDataComponentMap()
)

@Serializable
data class MutableUniverseGlobalData(
    var globalDataComponentMap: MutableGlobalDataComponentMap = MutableGlobalDataComponentMap()
)