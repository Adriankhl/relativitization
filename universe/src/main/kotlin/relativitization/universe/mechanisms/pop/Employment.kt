package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.EconomyData
import relativitization.universe.data.components.MutableEconomyData
import relativitization.universe.data.components.physics.MutableFuelRestMassData
import relativitization.universe.data.components.popsystem.MutableCarrierData
import relativitization.universe.data.components.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.components.popsystem.pop.scholar.MutableScholarPopData
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
                mutablePlayerData.playerInternalData.economyData(),
                universeData3DAtPlayer,
            )
        }

        return listOf()
    }

    fun updateEmployment(
        gamma: Double,
        carrierData: MutableCarrierData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        updateLabourerEmployment(
            gamma,
            carrierData.allPopData.labourerPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
            universeData3DAtPlayer,
        )

        updateScholarEmployment(
            gamma,
            carrierData.allPopData.scholarPopData,
            fuelRestMassData
        )
    }

    fun updateLabourerEmployment(
        gamma: Double,
        labourerPopData: MutableLabourerPopData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        economyData: EconomyData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        val salary: Double = labourerPopData.commonPopData.salary * gamma

        val incomeTax: Double = economyData.taxData.taxRateData.incomeTax.getIncomeTax(salary)

        // Available labourer
        val availableLabourer: Double = labourerPopData.commonPopData.adultPopulation

        // Accumulated employee
        var employeeAcc: Double = 0.0

        // Self factory first
        labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
            val maxPay: Double = maxNumEmployee * salary
            val tax: Double = maxPay * incomeTax
            val maxPayWithTax: Double = maxPay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining labourer and fuel
            if (((availableFuel - maxPayWithTax) > 0.0) && ((availableLabourer - employeeAcc - maxNumEmployee > 0.0))) {
                // Update number of employee
                it.lastNumEmployee = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding

                // Pay salary and tax here
                fuelRestMassData.production -= maxPayWithTax
                labourerPopData.commonPopData.saving += maxPay
                mutableEconomyData.taxData.storedFuelRestMass += tax

                // Accumulate employee
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
            val maxPayWithTax: Double = maxPay * (1.0 + incomeTax)

            // Decide employee and payment based on the remaining labourer and fuel
            if (((it.storedFuelRestMass - maxPayWithTax) > 0.0) && ((availableLabourer - employeeAcc - maxNumEmployee > 0.0))) {
                // Update number of employee
                it.lastNumEmployee = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding

                // Pay salary and tax here, it does not use the player fuel
                it.storedFuelRestMass -= maxPayWithTax
                labourerPopData.commonPopData.saving += maxPay
                mutableEconomyData.taxData.storedFuelRestMass += maxPay * incomeTax

                // Accumulate employee
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Compute unemployment rate
        labourerPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableLabourer)
    }

    fun updateScholarEmployment(
        gamma: Double,
        scholarPopData: MutableScholarPopData,
        fuelRestMassData: MutableFuelRestMassData,
    ) {
        val salary: Double = scholarPopData.commonPopData.salary * gamma

        // Available fuel to pay as salary
        val availableFuel: Double = fuelRestMassData.production

        // Available scholar
        val availableScholar: Double = scholarPopData.commonPopData.adultPopulation

        // Accumulated paid fuel
        var payAcc: Double = 0.0

        // Accumulated employee
        var employeeAcc: Double = 0.0


        // Self factory first
        scholarPopData.instituteMap.values.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val maxPay: Double = maxNumEmployee * salary

            // Decide employee and payment based on the remaining scholar and fuel
            if (((availableFuel - payAcc - maxPay) > 0.0) && ((availableScholar - employeeAcc - maxNumEmployee > 0.0))) {
                it.lastNumEmployee = it.maxNumEmployee
                // Accumulate salary and employee
                payAcc += maxPay
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Update data, consume fuel and pay salary
        scholarPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableScholar)
        scholarPopData.commonPopData.saving += payAcc
        fuelRestMassData.production -= payAcc
    }
}