package relativitization.universe.game.mechanisms.defaults.dilated.production

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.commands.SendFuelCommand
import relativitization.universe.game.data.components.MutablePhysicsData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.employeeFraction
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object FuelFactoryProduction : Mechanism() {
    // Parameters
    // max fuel produced per cube in space, prevent unlimited fuel and population
    private const val maxFuelPerCube: Double = 1E9
    // max multiplier from factory's experience
    private const val maxExperienceMultiplier: Double = 2.0

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val totalFuelProductionInNeighbor: Double =
            universeData3DAtPlayer.getNeighbourInCube(1).sumOf { playerData ->
                playerData.playerInternalData.popSystemData().carrierDataMap.values.sumOf { carrierData ->
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                        fuelFactoryData.lastOutputAmount
                    }
                }
            }

        val totalFuelProductionInPlayer: Double =
            mutablePlayerData.playerInternalData.popSystemData()
                .carrierDataMap.values.sumOf { carrierData ->
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                        computeOutputAmount(fuelFactoryData)
                    }
                }

        val totalFuelProductionInCube: Double =
            totalFuelProductionInNeighbor + totalFuelProductionInPlayer

        val maxFuelPerCubeFactor: Double = if (totalFuelProductionInCube > maxFuelPerCube) {
            maxFuelPerCube / totalFuelProductionInCube
        } else {
            1.0
        }

        val isFuelProductionEnable: Boolean = mutablePlayerData.playerInternalData.modifierData()
            .physicsModifierData.disableRestMassIncreaseTimeLimit <= 0

        // Do self factory production first if it is not disabled
        if (isFuelProductionEnable) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter { factory ->
                    (factory.ownerPlayerId == mutablePlayerData.playerId) && (factory.isOpened)
                }.forEach { factory ->
                    updateFuelData(
                        factory,
                        mutablePlayerData.playerInternalData.physicsData(),
                        maxFuelPerCubeFactor,
                    )
                }
            }
        }

        // Production by factory owned by other
        val logisticCommandList: List<Command> = if (isFuelProductionEnable) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter { factory ->
                    (factory.ownerPlayerId != mutablePlayerData.playerId) && (factory.isOpened)
                }.map { factory ->
                    computeSendFuelCommand(
                        factory,
                        mutablePlayerData,
                        maxFuelPerCubeFactor,
                    )
                }
            }.flatten()
        } else {
            listOf()
        }

        return logisticCommandList
    }


    /**
     * How much productivity is used due to the limitation of resource
     *
     * @param mutableFuelFactoryData the factory producing this resource
     */
    fun productAmountFraction(
        mutableFuelFactoryData: MutableFuelFactoryData,
    ): Double {
        val employeeFraction: Double = mutableFuelFactoryData.employeeFraction()

        return listOf(
            1.0,
            employeeFraction,
        ).minOrNull() ?: 0.0
    }

    /**
     * Compute how much the experience of a factory increases the output
     */
    fun computeExperienceMultiplier(
        experience: Double,
    ): Double {
        val multiplier: Double = 1.0 + experience * 0.1

        return min(max(multiplier, 1.0), maxExperienceMultiplier)
    }

    /**
     * Compute the output amount of fuel factory, ignoring maxFuelPerCube
     */
    fun computeOutputAmount(
        mutableFuelFactoryData: MutableFuelFactoryData
    ): Double {
        val amountFraction: Double = productAmountFraction(
            mutableFuelFactoryData,
        )

        val experienceMultiplier: Double =
            computeExperienceMultiplier(mutableFuelFactoryData.experience)

        return mutableFuelFactoryData.maxNumEmployee *
                mutableFuelFactoryData.fuelFactoryInternalData.maxOutputAmountPerEmployee *
                amountFraction * experienceMultiplier
    }

    /**
     * Consume and produce resource
     *
     * @param mutableFuelFactoryData the factory producing this resource
     * @param physicsData physics data of the player
     * @param maxFuelPerCubeFactor affected by how many fuel factory in a space unit
     */
    fun updateFuelData(
        mutableFuelFactoryData: MutableFuelFactoryData,
        physicsData: MutablePhysicsData,
        maxFuelPerCubeFactor: Double,
    ) {
        val outputAmount: Double = computeOutputAmount(mutableFuelFactoryData) * maxFuelPerCubeFactor

        mutableFuelFactoryData.lastOutputAmount = outputAmount

        // Produce fuel
        physicsData.addExternalFuel(outputAmount)
    }

    /**
     * Produce resource and send resource to owner
     *
     * @param mutableFuelFactoryData the factory to produce resource
     * @param mutablePlayerData the player of that factory
     */
    fun computeSendFuelCommand(
        mutableFuelFactoryData: MutableFuelFactoryData,
        mutablePlayerData: MutablePlayerData,
        maxFuelPerCubeFactor: Double,
    ): Command {
        val toId: Int = mutableFuelFactoryData.ownerPlayerId

        val outputAmount: Double = computeOutputAmount(mutableFuelFactoryData) * maxFuelPerCubeFactor

        mutableFuelFactoryData.lastOutputAmount = outputAmount

        return SendFuelCommand(
            toId = toId,
            amount = outputAmount,
            senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData()
                .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
        )
    }
}