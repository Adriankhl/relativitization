package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.physics.MutableFuelRestMassData
import relativitization.universe.data.components.popsystem.MutableCarrierData
import relativitization.universe.data.components.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

object Employment : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )


        val fuelRestMassData: MutableFuelRestMassData =
            mutablePlayerData.playerInternalData.physicsData().fuelRestMassData

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach {
            updateEmployment(
                gamma,
                it,
                fuelRestMassData,
                universeData3DAtPlayer,
            )
        }

        return listOf()
    }

    fun updateEmployment(
        gamma: Double,
        carrierData: MutableCarrierData,
        fuelRestMassData: MutableFuelRestMassData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        updateLabourerEmployment(
            gamma,
            carrierData.allPopData.labourerPopData,
            fuelRestMassData,
            universeData3DAtPlayer,
        )
    }

    fun updateLabourerEmployment(
        gamma: Double,
        labourerPopData: MutableLabourerPopData,
        fuelRestMassData: MutableFuelRestMassData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        val salary: Double = labourerPopData.commonPopData.salary * gamma

        // Available fuel to pay as salary
        val availableFuel: Double = fuelRestMassData.production

        // Available labourer
        val availableLabourer: Double = labourerPopData.commonPopData.adultPopulation

        // Accumulated paid fuel
        var payAcc: Double = 0.0

        // Accumulated labourer
        var employeeAcc: Double = 0.0

        // Self factory first
        labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
            val maxPay: Double = maxNumEmployee * salary

            // Decide employee and payment based on the remaining labourer and fuel
            if (((availableFuel - payAcc - maxPay) > 0.0) && ((availableLabourer - employeeAcc - maxNumEmployee > 0.0))) {
                it.lastNumEmployee = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
                // Accumulate salary and employee
                payAcc += maxPay
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Other player factory, don't pay from player fuel storage here
        labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
            val maxPay: Double = maxNumEmployee * salary

            // Decide employee and payment based on the remaining labourer and fuel
            if (((it.storedFuelRestMass - maxPay) > 0.0) && ((availableLabourer - employeeAcc - maxNumEmployee > 0.0))) {
                it.lastNumEmployee = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
                // Pay salary
                it.storedFuelRestMass -= maxPay
                // Accumulate employee
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Update data, consume fuel and pay salary
        labourerPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableLabourer)
        labourerPopData.commonPopData.saving += payAcc
        fuelRestMassData.production -= payAcc
    }
}