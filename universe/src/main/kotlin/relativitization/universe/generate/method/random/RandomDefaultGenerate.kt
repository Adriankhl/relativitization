package relativitization.universe.generate.method.random

import relativitization.universe.data.*
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.maths.grid.Grids

object RandomDefaultGenerate : RandomGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

        val mutableUniverseData4D = MutableUniverseData4D(
            Grids.create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        val mutableUniverseGlobalData = MutableUniverseGlobalData()

        return UniverseData(
            universeData4D = DataSerializer.copy(mutableUniverseData4D),
            universeSettings = universeSettings,
            universeState = UniverseState(0),
            commandMap = mutableMapOf(),
            universeGlobalData = DataSerializer.copy((mutableUniverseGlobalData)),
        )
    }
}