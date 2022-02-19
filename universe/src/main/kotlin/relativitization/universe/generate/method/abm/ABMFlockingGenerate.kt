package relativitization.universe.generate.method.abm

import relativitization.universe.ai.ABMFlockingAI
import relativitization.universe.ai.name
import relativitization.universe.data.*
import relativitization.universe.data.components.defaults.physics.MutableVelocity
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.components.syncData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.maths.random.Rand

object ABMFlockingGenerate : ABMGenerateUniverseMethod() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

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
            maxPlayerId = settings.numPlayer,
        )

        for (i in 1..settings.numPlayer) {
            val playerData = MutablePlayerData(playerId = i)

            // Set human player for small i
            if (i <= settings.numHumanPlayer) {
                playerData.playerType = PlayerType.HUMAN
            }

            playerData.int4D.x = Rand.rand().nextInt(0, universeSettings.xDim)
            playerData.int4D.y = Rand.rand().nextInt(0, universeSettings.yDim)
            playerData.int4D.z = Rand.rand().nextInt(0, universeSettings.zDim)

            playerData.playerInternalData.popSystemData().addSpaceShip(
                1.0, 1E5, 1E6
            )

            // Add fuel rest mass
            playerData.playerInternalData.physicsData().fuelRestMassData.movement = 1E6


            val vx = Rand.rand().nextDouble(-1.0, 1.0)
            val vy = Rand.rand().nextDouble(-1.0, 1.0)
            val vz = Rand.rand().nextDouble(-1.0, 1.0)

            // Constant velocity 0.5
            playerData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(0.5)

            // Use flocking ai
            playerData.playerInternalData.aiName = ABMFlockingAI.name()

            playerData.syncData()
            data.addPlayerDataToLatestWithAfterImage(
                playerData,
                universeState.getCurrentTime(),
                universeSettings.groupEdgeLength,
                universeSettings.playerAfterImageDuration
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = UniverseGlobalData()
        )
    }
}