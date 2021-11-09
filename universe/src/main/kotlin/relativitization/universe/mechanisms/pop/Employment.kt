package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.EconomyData
import relativitization.universe.data.components.MutableEconomyData
import relativitization.universe.data.components.physics.MutableFuelRestMassData
import relativitization.universe.data.components.popsystem.MutableCarrierData
import relativitization.universe.data.components.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.popsystem.pop.engineer.MutableEngineerPopData
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

        updateEngineerEmployment(
            gamma,
            carrierData.allPopData.engineerPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
        )

        updateScholarEmployment(
            gamma,
            carrierData.allPopData.scholarPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
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

        // Available population to work
        val availableEmployee: Double = labourerPopData.commonPopData.adultPopulation

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
            if (((availableFuel - maxPayWithTax) > 0.0) && ((availableEmployee - employeeAcc - maxNumEmployee > 0.0))) {
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
            if (((it.storedFuelRestMass - maxPayWithTax) > 0.0) && ((availableEmployee - employeeAcc - maxNumEmployee > 0.0))) {
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
        labourerPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableEmployee)
    }

    fun updateScholarEmployment(
        gamma: Double,
        scholarPopData: MutableScholarPopData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        economyData: EconomyData,
    ) {
        val salary: Double = scholarPopData.commonPopData.salary * gamma

        val incomeTax: Double = economyData.taxData.taxRateData.incomeTax.getIncomeTax(salary)

        // Available population to work
        val availableEmployee: Double = scholarPopData.commonPopData.adultPopulation

        // Accumulated employee
        var employeeAcc: Double = 0.0


        scholarPopData.instituteMap.values.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val maxPay: Double = maxNumEmployee * salary
            val tax: Double = maxPay * incomeTax
            val maxPayWithTax: Double = maxPay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (((availableFuel - maxPayWithTax) > 0.0) && ((availableEmployee - employeeAcc - maxNumEmployee > 0.0))) {
                // Update number of employee
                it.lastNumEmployee = it.maxNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= maxPayWithTax
                scholarPopData.commonPopData.saving += maxPay
                mutableEconomyData.taxData.storedFuelRestMass += tax

                // Accumulate employee
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Compute unemployment rate
        scholarPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableEmployee)
    }


    fun updateEngineerEmployment(
        gamma: Double,
        engineerPopData: MutableEngineerPopData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        economyData: EconomyData,
    ) {
        val salary: Double = engineerPopData.commonPopData.salary * gamma

        val incomeTax: Double = economyData.taxData.taxRateData.incomeTax.getIncomeTax(salary)

        // Available population to work
        val availableEmployee: Double = engineerPopData.commonPopData.adultPopulation

        // Accumulated employee
        var employeeAcc: Double = 0.0


        engineerPopData.laboratoryMap.values.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val maxPay: Double = maxNumEmployee * salary
            val tax: Double = maxPay * incomeTax
            val maxPayWithTax: Double = maxPay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (((availableFuel - maxPayWithTax) > 0.0) && ((availableEmployee - employeeAcc - maxNumEmployee > 0.0))) {
                // Update number of employee
                it.lastNumEmployee = it.maxNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= maxPayWithTax
                engineerPopData.commonPopData.saving += maxPay
                mutableEconomyData.taxData.storedFuelRestMass += tax

                // Accumulate employee
                employeeAcc += maxNumEmployee
            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Compute unemployment rate
        engineerPopData.commonPopData.unemploymentRate = (1.0 - employeeAcc / availableEmployee)
    }

    fun updateCommonEmployment(
        gamma: Double,
        commonPopData: MutableCommonPopData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        economyData: EconomyData,
    ) {
        val salary: Double = commonPopData.salary * gamma

        val incomeTax: Double = economyData.taxData.taxRateData.incomeTax.getIncomeTax(salary)

        val maxPay: Double = commonPopData.adultPopulation * salary
        val tax: Double = maxPay * incomeTax
        val maxPayWithTax: Double = maxPay + tax

        val availableFuel: Double = fuelRestMassData.production

        if (availableFuel >= maxPayWithTax) {
            commonPopData.unemploymentRate = 0.0

            // Pay salary and tax here
            fuelRestMassData.production -= maxPayWithTax
            commonPopData.saving += maxPay
            mutableEconomyData.taxData.storedFuelRestMass += tax
        } else {
            commonPopData.unemploymentRate = 1.0
        }
    }
}