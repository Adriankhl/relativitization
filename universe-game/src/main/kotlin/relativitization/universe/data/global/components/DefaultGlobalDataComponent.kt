package relativitization.universe.data.global.components

import kotlinx.serialization.Serializable

@Serializable
sealed class DefaultGlobalDataComponent : GlobalDataComponent() {
    companion object {
        fun createComponentList(): List<DefaultGlobalDataComponent> {
            return listOf(
                UniverseScienceData()
            )
        }
    }
}

@Serializable
sealed class MutableDefaultGlobalDataComponent : MutableGlobalDataComponent() {
    companion object {
        fun createComponentList(): List<MutableDefaultGlobalDataComponent> {
            return listOf(
                MutableUniverseScienceData()
            )
        }
    }
}