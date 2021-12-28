package relativitization.universe.generate.method.random

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name
import kotlin.test.Test

internal class RandomOneStarPerPlayerGenerateTest {
    @Test
    fun onePlayerTest() {
        val generateSetting = GenerateSettings(
            generateMethod = RandomOneStarPerPlayerGenerate.name(),
            numPlayer = 1,
            numHumanPlayer = 1,
            numExtraStellarSystem = 3,
            universeSettings = MutableUniverseSettings(
                universeName = "One player test",
                mechanismCollectionName = DefaultMechanismLists.name(),
                commandCollectionName = DefaultCommandAvailability.name(),
                globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                speedOfLight = 1.0,
                tDim = 8,
                xDim = 2,
                yDim = 2,
                zDim = 2,
                playerAfterImageDuration = 4,
                playerHistoricalInt4DLength = 4,
                groupEdgeLength = 0.01,
                otherSettings = mutableMapOf(),
            )
        )

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(generateSetting),
            "."
        )

        val view7 = universe.getUniverse3DViewAtPlayer(1)

        val commandList7: MutableList<Command> = mutableListOf()

        commandList7.add(
            ChangeProductionFuelTargetCommand(
                toId = 1,
                fromId = 1,
                fromInt4D = view7.get(1).int4D,
                targetAmount = 1E8
            )
        )

        commandList7.add(
            TransferFuelToProductionCommand(
                toId = 1,
                fromId = 1,
                fromInt4D = view7.get(1).int4D,
                amount = 1E7,
            )
        )

        (ResourceType.values().toList() - ResourceType.ENTERTAINMENT).forEach {
            commandList7.add(
                BuildForeignResourceFactoryCommand(
                    toId = 1,
                    fromId = 1,
                    fromInt4D = view7.get(1).int4D,
                    senderTopLeaderId = 1,
                    targetCarrierId = 0,
                    ownerId = 1,
                    resourceFactoryInternalData = view7.get(1).playerInternalData.playerScienceData()
                        .playerScienceApplicationData.newResourceFactoryInternalData(
                            it,
                            1.0
                        ),
                    qualityLevel = 1.0,
                    storedFuelRestMass = 0.0,
                    numBuilding = 1.0,
                )
            )
        }

        PopType.values().forEach {
            commandList7.add(
                ChangeSalaryCommand(
                    toId = 1,
                    fromId = 1,
                    fromInt4D = view7.get(1).int4D,
                    carrierId = 0,
                    popType = it,
                    salary = 1E-4,
                )
            )
        }

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to commandList7
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }
    }

    @Test
    fun onePlayerLongRunTest() {
        val generateSetting = GenerateSettings(
            generateMethod = RandomOneStarPerPlayerGenerate.name(),
            numPlayer = 1,
            numHumanPlayer = 1,
            numExtraStellarSystem = 3,
            universeSettings = MutableUniverseSettings(
                universeName = "One player test",
                mechanismCollectionName = DefaultMechanismLists.name(),
                commandCollectionName = DefaultCommandAvailability.name(),
                globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                speedOfLight = 1.0,
                tDim = 5,
                xDim = 1,
                yDim = 1,
                zDim = 1,
                playerAfterImageDuration = 4,
                playerHistoricalInt4DLength = 4,
                groupEdgeLength = 0.01,
                otherSettings = mutableMapOf(),
            )
        )

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(generateSetting),
            "."
        )

        runBlocking {
            for (turn in 1..100) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }

        val finalView = universe.getUniverse3DViewAtPlayer(1)

        println(finalView.getCurrentPlayerData().playerInternalData.popSystemData())
    }
}