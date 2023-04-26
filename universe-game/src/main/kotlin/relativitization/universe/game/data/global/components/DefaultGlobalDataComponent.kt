package relativitization.universe.game.data.global.components

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.global.components.GlobalDataComponent
import relativitization.universe.core.data.global.components.MutableGlobalDataComponent
import relativitization.universe.core.data.serializer.DataSerializer

@Serializable
sealed class DefaultGlobalDataComponent : GlobalDataComponent() {
    companion object {
        fun createComponentList(): List<DefaultGlobalDataComponent> {
            return DataSerializer.copy(MutableDefaultGlobalDataComponent.createComponentList())
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