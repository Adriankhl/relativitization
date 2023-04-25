package relativitization.universe.core.generate.empty

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.MutableUniverseData4D
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseData4D
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.UniverseState
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethod
import relativitization.universe.core.maths.grid.Grids
import kotlin.random.Random

object EmptyUniverse : GenerateUniverseMethod() {
    override fun name(): String = "Empty"

    override fun generate(generateSettings: GenerateSettings, random: Random): UniverseData {
        val mutableUniverseData4D = MutableUniverseData4D(
            Grids.create4DGrid(
                generateSettings.universeSettings.tDim,
                generateSettings.universeSettings.xDim,
                generateSettings.universeSettings.yDim,
                generateSettings.universeSettings.zDim
            ) { _, _, _, _ -> mutableMapOf() }
        )

        mutableUniverseData4D.addPlayerDataToLatestDuration(
            mutablePlayerData = MutablePlayerData(1),
            currentTime = 0,
            duration = 0,
            edgeLength = generateSettings.universeSettings.groupEdgeLength
        )

        return UniverseData(
            universeData4D = DataSerializer.copy(mutableUniverseData4D),
            universeSettings = UniverseSettings(),
            universeState = UniverseState(0),
            commandMap = mutableMapOf(),
            universeGlobalData = UniverseGlobalData(),
        )
    }
}