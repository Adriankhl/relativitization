package relativitization.universe.mechanisms.defaults.dilated.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendFuelCommand
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object FuelFactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        // max fuel produced per cube in space, prevent unlimited fuel and population
        val maxFuelPerCube: Double = 1E9

        val totalFuelProductionInCube: Double =
            universeData3DAtPlayer.getNeighbourAndSelf(0).sumOf { playerData ->
                playerData.playerInternalData.popSystemData().carrierDataMap.values.sumOf { carrierData ->
                    carrierData.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                        fuelFactoryData.numBuilding * fuelFactoryData.fuelFactoryInternalData.maxOutputAmount
                    }
                }
            }

        val maxFuelPerCubeFactor: Double = if (totalFuelProductionInCube > maxFuelPerCube) {
            maxFuelPerCube / totalFuelProductionInCube
        } else {
            1.0
        }

        // Do self factory production first if it is not disabled
        if (mutablePlayerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter { factory ->
                    factory.ownerPlayerId == mutablePlayerData.playerId
                }.forEach { factory ->
                    updateResourceData(
                        factory,
                        mutablePlayerData.playerInternalData.physicsData(),
                        maxFuelPerCubeFactor,
                    )
                }
            }
        }

        // Production by factory owned by other
        val logisticCommandList: List<Command> =
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter { factory ->
                    factory.ownerPlayerId != mutablePlayerData.playerId
                }.map { factory ->
                    computeSendFuelCommand(
                        factory,
                        mutablePlayerData,
                        maxFuelPerCubeFactor,
                    )
                }
            }.flatten()



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
     * Consume and produce resource
     *
     * @param mutableFuelFactoryData the factory producing this resource
     * @param physicsData physics data of the player
     * @param maxFuelPerCubeFactor affected by how many fuel factory in a space unit
     */
    fun updateResourceData(
        mutableFuelFactoryData: MutableFuelFactoryData,
        physicsData: MutablePhysicsData,
        maxFuelPerCubeFactor: Double,
    ) {
        val amountFraction: Double = productAmountFraction(
            mutableFuelFactoryData,
        )


        val outputAmount: Double =
            mutableFuelFactoryData.fuelFactoryInternalData.maxOutputAmount * amountFraction *
                    mutableFuelFactoryData.numBuilding * maxFuelPerCubeFactor

        mutableFuelFactoryData.lastOutputAmount = outputAmount

        // Produce fuel
        physicsData.addFuel(outputAmount)
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


        val amountFraction: Double = productAmountFraction(
            mutableFuelFactoryData,
        )


        val outputAmount: Double =
            mutableFuelFactoryData.fuelFactoryInternalData.maxOutputAmount * amountFraction *
                    mutableFuelFactoryData.numBuilding * maxFuelPerCubeFactor

        mutableFuelFactoryData.lastOutputAmount = outputAmount

        return SendFuelCommand(
            toId = toId,
            fromId = mutablePlayerData.playerId,
            fromInt4D = mutablePlayerData.int4D.toInt4D(),
            amount = outputAmount,
            senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
        )
    }
}