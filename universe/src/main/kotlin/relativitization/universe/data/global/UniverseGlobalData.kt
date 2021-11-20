package relativitization.universe.data.global

import kotlinx.serialization.Serializable
import relativitization.universe.data.global.components.*
import kotlin.reflect.full.createInstance

@Serializable
data class UniverseGlobalData(
    val globalDataComponentMap: GlobalDataComponentMap = GlobalDataComponentMap(
        DefaultGlobalDataComponent::class.sealedSubclasses.map { it.createInstance() },
    )
) {
    fun universeScienceData(): UniverseScienceData =
        globalDataComponentMap.getOrDefault(UniverseScienceData::class, UniverseScienceData())
}

@Serializable
data class MutableUniverseGlobalData(
    var globalDataComponentMap: MutableGlobalDataComponentMap = MutableGlobalDataComponentMap(
        MutableDefaultGlobalDataComponent::class.sealedSubclasses.map { it.createInstance() },
    )
) {
    fun universeScienceData(): MutableUniverseScienceData =
        globalDataComponentMap.getOrDefault(
            MutableUniverseScienceData::class,
            MutableUniverseScienceData()
        )

    fun universeScienceData(newUniverseScienceData: MutableUniverseScienceData) {
        globalDataComponentMap.put(newUniverseScienceData)
    }
}