package relativitization.universe

import relativitization.universe.data.UniverseData

class Universe(private val universeData: UniverseData) {
    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim
}