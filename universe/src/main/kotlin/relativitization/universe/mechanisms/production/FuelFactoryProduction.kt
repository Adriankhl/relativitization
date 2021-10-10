package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendFuelCommand
import relativitization.universe.data.component.MutablePhysicsData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

object FuelFactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val gamma: Double = Relativistic.gamma(
            mutablePlayerData.velocity.toVelocity(),
            universeSettings.speedOfLight
        )


        // Do self factory production first
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.fuelFactoryMap.values.filter { factory ->
                factory.ownerPlayerId == mutablePlayerData.playerId
            }.forEach { factory ->
                updateResourceData(
                    factory,
                    mutablePlayerData.playerInternalData.physicsData(),
                    gamma,
                )
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
                        gamma
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
     * @param resourceData the amount of resource owned by the player
     * @param physicsData physics data of the player
     * @param gamma Lorentz factor
     */
    fun updateResourceData(
        mutableFuelFactoryData: MutableFuelFactoryData,
        physicsData: MutablePhysicsData,
        gamma: Double
    ) {
        val amountFraction: Double = productAmountFraction(
            mutableFuelFactoryData,
        )


        val outputAmount: Double = mutableFuelFactoryData.fuelFactoryInternalData.maxOutputAmount * amountFraction * mutableFuelFactoryData.numBuilding / gamma

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
        gamma: Double,
    ): Command {
        val toId: Int = mutableFuelFactoryData.ownerPlayerId


        val amountFraction: Double = productAmountFraction(
            mutableFuelFactoryData,
        )


        val outputAmount: Double = mutableFuelFactoryData.fuelFactoryInternalData.maxOutputAmount * amountFraction * mutableFuelFactoryData.numBuilding / gamma

        mutableFuelFactoryData.lastOutputAmount = outputAmount

        return SendFuelCommand(
            toId = toId,
            fromId = mutablePlayerData.playerId,
            fromInt4D = mutablePlayerData.int4D.toInt4D(),
            amount = outputAmount,
            senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance,
        )
    }
}