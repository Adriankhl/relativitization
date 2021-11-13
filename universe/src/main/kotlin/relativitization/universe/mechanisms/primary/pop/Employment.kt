package relativitization.universe.mechanisms.primary.pop

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
import relativitization.universe.data.components.popsystem.pop.soldier.MutableSoldierPopData
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

        updateSoldierEmployment(
            gamma,
            carrierData.allPopData.soldierPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
        )

        updateCommonEmployment(
            gamma,
            carrierData.allPopData.entertainerPopData.commonPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
        )

        updateCommonEmployment(
            gamma,
            carrierData.allPopData.servicePopData.commonPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
        )

        updateCommonEmployment(
            gamma,
            carrierData.allPopData.medicPopData.commonPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
        )

        updateCommonEmployment(
            gamma,
            carrierData.allPopData.educatorPopData.commonPopData,
            fuelRestMassData,
            mutableEconomyData,
            universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.economyData(),
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

    /**
     * Distribute the labourer among fuel factories and resource factories
     * Prioritize self factories than other factories, fuel factories than resource factories
     */
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

        // Total number of max employee of self fuel factory
        val maxSelfFuelFactoryEmployee: Double = labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.fold(0.0) { acc, mutableFuelFactoryData ->
            acc + mutableFuelFactoryData.fuelFactoryInternalData.maxNumEmployee * mutableFuelFactoryData.numBuilding
        }

        // Total number of max employee of self resource factory
        val maxSelfResourceFactoryEmployee: Double =
            labourerPopData.resourceFactoryMap.values.filter {
                it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
            }.fold(0.0) { acc, mutableResourceFactoryData ->
                acc + mutableResourceFactoryData.resourceFactoryInternalData.maxNumEmployee * mutableResourceFactoryData.numBuilding
            }

        // Total number of max employee of other fuel factory
        val maxOtherFuelFactoryEmployee: Double = labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.fold(0.0) { acc, mutableFuelFactoryData ->
            acc + mutableFuelFactoryData.fuelFactoryInternalData.maxNumEmployee * mutableFuelFactoryData.numBuilding
        }


        // Total number of max employee of other resource factory
        val maxOtherResourceFactoryEmployee: Double =
            labourerPopData.resourceFactoryMap.values.filter {
                it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId
            }.fold(0.0) { acc, mutableResourceFactoryData ->
                acc + mutableResourceFactoryData.resourceFactoryInternalData.maxNumEmployee * mutableResourceFactoryData.numBuilding
            }

        // Compute fractions of employee if number of available employees is not enough
        val selfFuelFactoryEmployeeFraction: Double =
            when {
                maxSelfFuelFactoryEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee > 0.0 -> {
                    maxSelfFuelFactoryEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }

        val selfResourceFactoryEmployeeFraction: Double =
            when {
                maxSelfFuelFactoryEmployee + maxSelfResourceFactoryEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee - maxSelfFuelFactoryEmployee > 0.0 -> {
                    maxSelfResourceFactoryEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }


        val otherFuelFactoryEmployeeFraction: Double =
            when {
                maxSelfFuelFactoryEmployee + maxSelfResourceFactoryEmployee + maxOtherFuelFactoryEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee - maxSelfFuelFactoryEmployee - maxSelfResourceFactoryEmployee > 0.0 -> {
                    maxOtherFuelFactoryEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }


        val otherResourceFactoryEmployeeFraction: Double =
            when {
                maxSelfFuelFactoryEmployee + maxSelfResourceFactoryEmployee + maxOtherFuelFactoryEmployee + maxOtherResourceFactoryEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee - maxSelfFuelFactoryEmployee - maxSelfResourceFactoryEmployee - maxOtherFuelFactoryEmployee > 0.0 -> {
                    maxOtherResourceFactoryEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }



        // Self factory first
        labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
            val newNumEmployee: Double = maxNumEmployee * selfFuelFactoryEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining labourer and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= payWithTax
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Self factory first
        labourerPopData.resourceFactoryMap.values.filter {
            it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.resourceFactoryInternalData.maxNumEmployee * it.numBuilding
            val newNumEmployee: Double = maxNumEmployee * selfResourceFactoryEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining labourer and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= payWithTax
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Other player factory, don't pay from player fuel storage here
        labourerPopData.fuelFactoryMap.values.filter {
            it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.fuelFactoryInternalData.maxNumEmployee * it.numBuilding
            val newNumEmployee: Double = maxNumEmployee * otherFuelFactoryEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val payWithTax: Double = pay * (1.0 + incomeTax)

            // Decide employee and payment based on the remaining labourer and fuel
            if (it.storedFuelRestMass - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here, it does not use the player fuel
                it.storedFuelRestMass -= payWithTax
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += pay * incomeTax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Other player factory, don't pay from player fuel storage here
        labourerPopData.resourceFactoryMap.values.filter {
            it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId
        }.forEach {

            val maxNumEmployee: Double = it.resourceFactoryInternalData.maxNumEmployee * it.numBuilding
            val newNumEmployee: Double = maxNumEmployee * otherResourceFactoryEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val payWithTax: Double = pay * (1.0 + incomeTax)

            // Decide employee and payment based on the remaining labourer and fuel
            if (it.storedFuelRestMass - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here, it does not use the player fuel
                it.storedFuelRestMass -= payWithTax
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += pay * incomeTax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double = labourerPopData.fuelFactoryMap.values.fold(0.0) { acc, mutableFuelFactoryData ->
            acc + mutableFuelFactoryData.lastNumEmployee
        } + labourerPopData.resourceFactoryMap.values.fold(0.0) { acc, mutableResourceFactoryData ->
            acc + mutableResourceFactoryData.lastNumEmployee
        }

        // Compute unemployment rate
        labourerPopData.commonPopData.unemploymentRate = (1.0 - actualNumEmployee / availableEmployee)
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

        // Maximum scholar employee in institutes
        val maxInstituteEmployee: Double = scholarPopData.instituteMap.values.fold(0.0) { acc, mutableInstituteData ->
            acc + mutableInstituteData.maxNumEmployee
        }


        // Compute fractions of employee if number of available employees is not enough
        val instituteEmployeeFraction: Double =
            when {
                maxInstituteEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee > 0.0 -> {
                    maxInstituteEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }


        scholarPopData.instituteMap.values.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * instituteEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = it.maxNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= payWithTax
                scholarPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double = scholarPopData.instituteMap.values.fold(0.0) { acc, mutableInstituteData ->
            acc + mutableInstituteData.lastNumEmployee
        }

        // Compute unemployment rate
        scholarPopData.commonPopData.unemploymentRate = (1.0 - actualNumEmployee / availableEmployee)
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

        // Maximum scholar employee in laboratories
        val maxLaboratoryEmployee: Double = engineerPopData.laboratoryMap.values.fold(0.0) { acc, mutableLaboratoryData ->
            acc + mutableLaboratoryData.maxNumEmployee
        }


        // Compute fractions of employee if number of available employees is not enough
        val laboratoryEmployeeFraction: Double =
            when {
                maxLaboratoryEmployee > availableEmployee -> {
                    1.0
                }
                availableEmployee > 0.0 -> {
                    maxLaboratoryEmployee / availableEmployee
                }
                else -> {
                    0.0
                }
            }


        engineerPopData.laboratoryMap.values.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * laboratoryEmployeeFraction
            val pay: Double = newNumEmployee * salary
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = it.maxNumEmployee

                // Pay salary and tax here
                fuelRestMassData.production -= payWithTax
                engineerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double = engineerPopData.laboratoryMap.values.fold(0.0) { acc, mutableLaboratoryData ->
            acc + mutableLaboratoryData.lastNumEmployee
        }

        // Compute unemployment rate
        engineerPopData.commonPopData.unemploymentRate = (1.0 - actualNumEmployee / availableEmployee)
    }

    /**
     * Update soldier employment
     */
    fun updateSoldierEmployment(
        gamma: Double,
        soldierPopData: MutableSoldierPopData,
        fuelRestMassData: MutableFuelRestMassData,
        mutableEconomyData: MutableEconomyData,
        economyData: EconomyData,
    ) {
        val salary: Double = soldierPopData.commonPopData.salary * gamma

        val incomeTax: Double = economyData.taxData.taxRateData.incomeTax.getIncomeTax(salary)

        val maxPay: Double = soldierPopData.commonPopData.adultPopulation * salary
        val tax: Double = maxPay * incomeTax
        val maxPayWithTax: Double = maxPay + tax

        val availableFuel: Double = fuelRestMassData.production

        if (availableFuel >= maxPayWithTax) {
            soldierPopData.commonPopData.unemploymentRate = 0.0

            // Update military base employment
            soldierPopData.militaryBaseData.lastNumEmployee = soldierPopData.commonPopData.adultPopulation

            // Pay salary and tax here
            fuelRestMassData.production -= maxPayWithTax
            soldierPopData.commonPopData.saving += maxPay
            mutableEconomyData.taxData.storedFuelRestMass += tax
        } else {
            soldierPopData.commonPopData.unemploymentRate = 1.0

            // Update military base employment
            soldierPopData.militaryBaseData.lastNumEmployee = 0.0
        }
    }

    /**
     * Generic salary payment, pay all or nothing
     */
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