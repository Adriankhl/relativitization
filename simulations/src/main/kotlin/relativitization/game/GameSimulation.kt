package relativitization.game

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.maths.number.toScientificNotation
import relativitization.universe.mechanisms.DefaultMechanismLists

internal fun gameSingleRun(
    universeName: String = "Game",
    printStep: Boolean = false,
    randomSeed: Long = 100L,
    xDim: Int = 10,
    yDim: Int = 10,
    zDim: Int = 3,
    numStep: Int = 1000,
    numPlayer: Int = 0,
    numExtraStellarSystem: Int = 0,
    initialPopulation: Double = 1E6,
): DataFrame<*> {
    // This map will be converted to dataframe
    val dfMap: MutableMap<String, MutableList<Any>> = mutableMapOf()

    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 1,
        otherIntMap = mutableMapOf("numExtraStellarSystem" to numExtraStellarSystem),
        otherDoubleMap = mutableMapOf("initialPopulation" to initialPopulation),
        universeSettings = MutableUniverseSettings(
            universeName = universeName,
            commandCollectionName = DefaultCommandAvailability.name(),
            mechanismCollectionName = DefaultMechanismLists.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = xDim,
            yDim = yDim,
            zDim = zDim,
            randomSeed = randomSeed,
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        val currentPlayerDataList: List<PlayerData> = universe.getCurrentPlayerDataList()

        val numCarrier: Int = currentPlayerDataList.fold(0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().numCarrier()
        }

        val population: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
        }

        val fuelRestMass: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.physicsData().fuelRestMassData.total()
        }

        val fuelProduction: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().carrierDataMap.values
                .sumOf { carrier ->
                    carrier.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactory ->
                        fuelFactory.lastOutputAmount
                    }
                }
        }

        val saving: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalSaving()
        }

        val totalSatisfaction: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalSatisfaction()
        }

        val numAlliance: Int = currentPlayerDataList.fold(0) { acc, playerData ->
            acc + playerData.playerInternalData.diplomacyData().relationData.allyMap.size
        }

        val outputDataMap = mapOf(
            "randomSeed" to randomSeed,
            "turn" to turn,
            "numPlayer" to universe.availablePlayers().size,
            "dead" to universe.getDeadIdList().size,
            "numCarrier" to numCarrier,
            "population" to population,
            "fuelRestMass" to fuelRestMass,
            "fuelProduction" to fuelProduction,
            "saving" to saving,
            "averageSatisfaction" to totalSatisfaction / population,
            "numAlliance" to numAlliance,
        )

        outputDataMap.forEach {
            dfMap.getOrPut(it.key) {
                mutableListOf()
            }.add(it.value)
        }

        if (printStep) {
            outputDataMap.forEach {
                val value = it.value
                if (value is Double) {
                    print("${it.key}: ${value.toScientificNotation().toDouble(2)}, ")
                } else {
                    print("${it.key}: $value, ")
                }
            }
            print("\b\b.\n")
        }

        universe.pureAIStep()
    }

    return dfMap.toDataFrame()
}