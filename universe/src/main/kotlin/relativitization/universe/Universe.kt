package relativitization.universe

import relativitization.universe.data.UniverseData

class Universe(private val universeData: UniverseData) {
    private val xDim = universeData.universeSettings.xDim
    private val yDim = universeData.universeSettings.yDim
    private val zDim = universeData.universeSettings.zDim

    val playerCollection: PlayerCollection = PlayerCollection(xDim, yDim, zDim)
    init {
        // for each player data at the latest time slice, create player object and add to universe3D
        universeData.universeData4D.getLatest().flatten().flatten().flatten().forEach {
            playerCollection.addPlayer(it)
        }
    }
}