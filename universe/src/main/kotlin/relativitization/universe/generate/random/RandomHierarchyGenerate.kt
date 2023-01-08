package relativitization.universe.generate.random

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.maths.grid.Grids
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object RandomHierarchyGenerate : RandomGenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun name(): String = "Random hierarchy"

    override fun generate(
        generateSettings: GenerateSettings,
        random: Random
    ): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(
            generateSettings.universeSettings
        )

        val universeGlobalData: UniverseGlobalData =
            RandomOneStarPerPlayerGenerate.createUniverseGlobalData(random)

        val mutableUniverseData4D = MutableUniverseData4D(
            Grids.create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableMapOf() }
        )

        // Only consider numPlayer, ignore numExtraStellarSystem
        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 0,
        )

        val mutablePlayerDataList: List<MutablePlayerData> =
            RandomOneStarPerPlayerGenerate.createMutablePlayerList(
                generateSettings,
                universeSettings,
                universeGlobalData,
                universeState,
                random,
            )

        val playerIdMap: Map<Int, MutablePlayerData> =
            mutablePlayerDataList.associateBy { it.playerId }

        // Store the remained players to be picked as subordinate
        val remainedIdList: MutableList<Int> = playerIdMap.keys.shuffled(random).toMutableList()

        // maximum number of direct subordinate
        val subordinatePerIteration = 2

        // Top leader probability factor
        val hierarchySizeFactor: Int = generateSettings.numPlayer / 5

        // Iteratively construct hierarchy tree
        while(remainedIdList.size > 1) {
            // the player list for this iteration
            val turnList: MutableList<Int> = remainedIdList.toMutableList()
            while (turnList.size > 1) {
                val leaderId: Int = turnList[0]

                val leaderData: MutablePlayerData = playerIdMap.getValue(leaderId)

                // Probability to leave the remained list and be an independent player
                val independentProb: Double = leaderData.playerInternalData.subordinateIdSet
                    .size.toDouble() / hierarchySizeFactor

                if (random.nextDouble() < independentProb) {
                    turnList.remove(leaderId)
                    remainedIdList.remove(leaderId)
                } else {
                    val closestIds: List<Int> = turnList.sortedBy { otherId ->
                        Intervals.intDistance(
                            playerIdMap.getValue(leaderId).int4D.toInt3D(),
                            playerIdMap.getValue(otherId).int4D.toInt3D(),
                        )
                    }.take(subordinatePerIteration)

                    closestIds.forEach { subordinateId ->
                        val subordinateData: MutablePlayerData = playerIdMap.getValue(subordinateId)
                        leaderData.addDirectSubordinateId(subordinateId)
                        leaderData.playerInternalData.subordinateIdSet.addAll(
                            subordinateData.playerInternalData.subordinateIdSet
                        )

                        subordinateData.getSubordinateAndSelfIdSet().forEach { otherId ->
                            val otherData: MutablePlayerData = playerIdMap.getValue(otherId)
                            otherData.playerInternalData.leaderIdList.add(0, leaderId)
                        }
                    }

                    turnList.remove(leaderId)
                    turnList.removeAll(closestIds)
                    remainedIdList.removeAll(closestIds)
                }
            }
        }

        mutablePlayerDataList.forEach { mutablePlayerData ->
            mutableUniverseData4D.addPlayerDataToLatestDuration(
                mutablePlayerData = mutablePlayerData,
                currentTime = universeState.getCurrentTime(),
                duration = universeSettings.tDim - 1,
                edgeLength = universeSettings.groupEdgeLength
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(mutableUniverseData4D),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = universeGlobalData,
        )
    }
}