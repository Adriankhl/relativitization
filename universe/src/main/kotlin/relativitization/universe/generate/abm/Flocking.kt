package relativitization.universe.generate.abm

import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid

class Flocking : GenerateUniverse() {
    override fun generate(setting: GenerateSetting): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(setting.universeSettings)

        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = setting.numPlayer,
        )

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
        )
    }
}