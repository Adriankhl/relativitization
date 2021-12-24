package relativitization.universe.generate.method.random

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.ChangeProductionFuelTargetCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
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
}