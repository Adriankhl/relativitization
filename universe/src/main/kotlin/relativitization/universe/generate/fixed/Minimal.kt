package relativitization.universe.generate.fixed

import relativitization.universe.data.*
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid

class Minimal : GenerateUniverse() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = copy(settings.universeSettings)

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
            maxPlayerId = 4,
        )

        val playerData1 = MutablePlayerData(1)
        val playerData2 = MutablePlayerData(2)
        val playerData3 = MutablePlayerData(3)
        val playerData4 = MutablePlayerData(4)

        playerData1.playerType = PlayerType.HUMAN
        playerData3.int4D = MutableInt4D(0, 0, 0, 1)

        // Add one stellar to players
        playerData1.playerInternalData.popSystemicData.addRandomStellarSystem()
        playerData3.playerInternalData.popSystemicData.addRandomStellarSystem()

        // Add non zero fuel rest mass
        playerData1.playerInternalData.physicsData.fuelRestMass = 100.0
        playerData2.playerInternalData.physicsData.fuelRestMass = 200.0
        playerData3.playerInternalData.physicsData.fuelRestMass = 300.0

        // Allow player to use fuel
        playerData1.playerInternalData.physicsData.maxDeltaFuelRestMass = 50.0
        playerData2.playerInternalData.physicsData.maxDeltaFuelRestMass = 100.0
        playerData3.playerInternalData.physicsData.maxDeltaFuelRestMass = 150.0


        // player 1 is a leader of player 2
        playerData2.playerInternalData.changeDirectLeaderId(playerData1.id)
        playerData1.playerInternalData.addDirectSubordinateId(playerData2.id)

        // player 4 is a dead player
        playerData4.playerInternalData.isAlive = false

        // Change AI to EmptyAI
        playerData1.playerInternalData.aiData.aiName = "EmptyAI"
        playerData2.playerInternalData.aiData.aiName = "EmptyAI"
        playerData3.playerInternalData.aiData.aiName = "EmptyAI"
        playerData4.playerInternalData.aiData.aiName = "EmptyAI"

        data.addPlayerDataToLatestWithAfterImage(
            playerData1,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData2,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData3,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )
        data.addPlayerDataToLatestWithAfterImage(
            playerData4,
            universeState.getCurrentTime(),
            universeSettings.groupEdgeLength,
            universeSettings.playerAfterImageDuration
        )

        return UniverseData(
            universeData4D = copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeScienceData = UniverseScienceData(),
        )
    }
}