package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableGeneralPopSystemData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.components.defaults.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min

object Employment : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach {
            updateEmployment(
                it,
                mutablePlayerData.playerInternalData.popSystemData().generalPopSystemData,
                mutablePlayerData.playerInternalData.physicsData(),
                mutablePlayerData.playerInternalData.economyData(),
                universeData3DAtPlayer,
            )
        }

        return listOf()
    }

    fun updateEmployment(
        carrierData: MutableCarrierData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        updateLabourerEmployment(
            carrierData.allPopData.labourerPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
            universeData3DAtPlayer,
        )

        updateSoldierEmployment(
            carrierData.allPopData.soldierPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateCommonEmployment(
            carrierData.allPopData.entertainerPopData.commonPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateCommonEmployment(
            carrierData.allPopData.servicePopData.commonPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateCommonEmployment(
            carrierData.allPopData.medicPopData.commonPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateCommonEmployment(
            carrierData.allPopData.educatorPopData.commonPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateEngineerEmployment(
            carrierData.allPopData.engineerPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

        updateScholarEmployment(
            carrierData.allPopData.scholarPopData,
            mutableGeneralPopSystemData,
            mutablePhysicsData,
            mutableEconomyData,
        )

    }

    /**
     * Distribute the labourer among fuel factories and resource factories
     * Prioritize self factories than other factories, fuel factories than resource factories
     */
    fun updateLabourerEmployment(
        labourerPopData: MutableLabourerPopData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        val salaryPerEmployee: Double =
            labourerPopData.commonPopData.salaryPerEmployee(mutableGeneralPopSystemData)

        val incomeTax: Double =
            mutableEconomyData.taxData.taxRateData.incomeTax.getIncomeTax(salaryPerEmployee)

        // Available population to work
        val availableEmployee: Double = labourerPopData.commonPopData.adultPopulation

        // Clear employee in closed factory
        labourerPopData.fuelFactoryMap.values.filter {
            !it.isOpened
        }.forEach {
            it.lastNumEmployee = 0.0
        }
        labourerPopData.resourceFactoryMap.values.filter {
            !it.isOpened
        }.forEach {
            it.lastNumEmployee = 0.0
        }

        // Total number of max employee of self fuel factory, ignore factor of salary
        val maxSelfFuelFactoryEmployee: Double = labourerPopData.fuelFactoryMap.values.filter {
            (it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.fold(0.0) { acc, mutableFuelFactoryData ->
            acc + mutableFuelFactoryData.maxNumEmployee
        }

        // Total number of max employee of self resource factory, ignore factor of salary
        val maxSelfResourceFactoryEmployee: Double =
            labourerPopData.resourceFactoryMap.values.filter {
                (it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
            }.fold(0.0) { acc, mutableResourceFactoryData ->
                acc + mutableResourceFactoryData.maxNumEmployee
            }

        // Total number of max employee of other fuel factory, consider salary
        val maxOtherFuelFactoryEmployee: Double = labourerPopData.fuelFactoryMap.values.filter {
            (it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.fold(0.0) { acc, mutableFuelFactoryData ->
            val maxNumEmployee: Double = mutableFuelFactoryData.maxNumEmployee

            val maxPaidEmployee: Double = if (salaryPerEmployee > 0.0) {
                val payWithTax: Double = salaryPerEmployee * (1.0 + incomeTax)
                mutableFuelFactoryData.storedFuelRestMass / payWithTax
            } else {
                maxNumEmployee
            }
            acc + min(maxNumEmployee, maxPaidEmployee)
        }

        // Total number of max employee of other resource factory, consider salary
        val maxOtherResourceFactoryEmployee: Double =
            labourerPopData.resourceFactoryMap.values.filter {
                (it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
            }.fold(0.0) { acc, mutableResourceFactoryData ->
                val maxNumEmployee: Double = mutableResourceFactoryData.maxNumEmployee
                val maxPaidEmployee: Double = if (salaryPerEmployee > 0.0) {
                    val payWithTax: Double = salaryPerEmployee * (1.0 + incomeTax)
                    mutableResourceFactoryData.storedFuelRestMass / payWithTax
                } else {
                    maxNumEmployee
                }
                acc + min(maxNumEmployee, maxPaidEmployee)
            }

        // Compute fractions of employee if number of available employees is not enough
        val totalMaxEmployee: Double = maxSelfFuelFactoryEmployee +
                maxSelfResourceFactoryEmployee +
                maxOtherFuelFactoryEmployee +
                maxOtherResourceFactoryEmployee
        val employeeFraction: Double = if (totalMaxEmployee <= availableEmployee) {
            1.0
        } else {
            if (totalMaxEmployee > 0.0) {
                availableEmployee / totalMaxEmployee
            } else {
                0.0
            }
        }

        // Self fuel factory
        labourerPopData.fuelFactoryMap.values.filter {
            (it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * employeeFraction
            val pay: Double = newNumEmployee * salaryPerEmployee
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

            // Decide employee and payment based on the remaining labourer and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                mutablePhysicsData.removeInternalProductionFuel(payWithTax)
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Self resource factory
        labourerPopData.resourceFactoryMap.values.filter {
            (it.ownerPlayerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * employeeFraction
            val pay: Double = newNumEmployee * salaryPerEmployee
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

            // Decide employee and payment based on the remaining labourer and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                mutablePhysicsData.removeInternalProductionFuel(payWithTax)
                labourerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Other player factory, don't pay from player fuel storage here
        labourerPopData.fuelFactoryMap.values.filter {
            (it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee

            val maxNewNumEmployee: Double = maxNumEmployee * employeeFraction
            val maxPaidEmployee: Double = if (salaryPerEmployee > 0.0) {
                val singlePayWithTax: Double = salaryPerEmployee * (1.0 + incomeTax)
                it.storedFuelRestMass / singlePayWithTax
            } else {
                maxNewNumEmployee
            }
            val newNumEmployee: Double = min(
                maxNewNumEmployee,
                maxPaidEmployee,
            )
            val pay: Double = newNumEmployee * salaryPerEmployee
            val payWithTax: Double = pay * (1.0 + incomeTax)

            // Always hire employee

            // Update number of employee
            it.lastNumEmployee = newNumEmployee

            // Pay salary and tax here, it does not use the player fuel
            it.storedFuelRestMass -= payWithTax
            labourerPopData.commonPopData.saving += pay
            mutableEconomyData.taxData.storedFuelRestMass += pay * incomeTax
        }

        // Other player factory, don't pay from player fuel storage here
        labourerPopData.resourceFactoryMap.values.filter {
            (it.ownerPlayerId != universeData3DAtPlayer.getCurrentPlayerData().playerId) && (it.isOpened)
        }.forEach {

            val maxNumEmployee: Double = it.maxNumEmployee
            val maxNewNumEmployee: Double = maxNumEmployee * employeeFraction
            val maxPaidEmployee: Double = if (salaryPerEmployee > 0.0) {
                val singlePayWithTax: Double = salaryPerEmployee * (1.0 + incomeTax)
                it.storedFuelRestMass / singlePayWithTax
            } else {
                maxNewNumEmployee
            }
            val newNumEmployee: Double = min(
                maxNewNumEmployee,
                maxPaidEmployee,
            )
            val pay: Double = newNumEmployee * salaryPerEmployee
            val payWithTax: Double = pay * (1.0 + incomeTax)

            // Always hire employee

            // Decide employee and payment based on the remaining labourer and fuel
            // Update number of employee
            it.lastNumEmployee = newNumEmployee

            // Pay salary and tax here, it does not use the player fuel
            it.storedFuelRestMass -= payWithTax
            labourerPopData.commonPopData.saving += pay
            mutableEconomyData.taxData.storedFuelRestMass += pay * incomeTax
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double =
            labourerPopData.fuelFactoryMap.values.fold(0.0) { acc, mutableFuelFactoryData ->
                acc + mutableFuelFactoryData.lastNumEmployee
            } + labourerPopData.resourceFactoryMap.values.fold(0.0) { acc, mutableResourceFactoryData ->
                acc + mutableResourceFactoryData.lastNumEmployee
            }

        // Compute employment rate, allow a small error from floating point operation
        val actualEmploymentRate: Double = actualNumEmployee / availableEmployee
        labourerPopData.commonPopData.employmentRate = when {
            (actualEmploymentRate > 1.0) && (actualEmploymentRate < 1.01) -> 1.0
            (actualEmploymentRate > -0.01) && (actualEmploymentRate < 0.0) -> 0.0
            else -> actualEmploymentRate
        }
    }

    fun updateScholarEmployment(
        scholarPopData: MutableScholarPopData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
    ) {
        val salaryPerEmployee: Double =
            scholarPopData.commonPopData.salaryPerEmployee(mutableGeneralPopSystemData)

        val incomeTax: Double =
            mutableEconomyData.taxData.taxRateData.incomeTax.getIncomeTax(salaryPerEmployee)

        // Available population to work
        val availableEmployee: Double = scholarPopData.commonPopData.adultPopulation

        // Maximum scholar employee in institutes
        val maxInstituteEmployee: Double =
            scholarPopData.instituteMap.values.fold(0.0) { acc, mutableInstituteData ->
                acc + mutableInstituteData.instituteInternalData.maxNumEmployee
            }

        // Compute fractions of employee if number of available employees is not enough
        val instituteEmployeeFraction: Double =
            when {
                maxInstituteEmployee <= availableEmployee -> {
                    1.0
                }
                (availableEmployee > 0.0) && (maxInstituteEmployee > 0.0) -> {
                    availableEmployee / maxInstituteEmployee
                }
                else -> {
                    0.0
                }
            }

        scholarPopData.instituteMap.values.forEach {
            val maxNumEmployee: Double = it.instituteInternalData.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * instituteEmployeeFraction
            val pay: Double = newNumEmployee * salaryPerEmployee
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                mutablePhysicsData.removeInternalProductionFuel(payWithTax)
                scholarPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double =
            scholarPopData.instituteMap.values.fold(0.0) { acc, mutableInstituteData ->
                acc + mutableInstituteData.lastNumEmployee
            }

        // Compute employment rate, allow a small error from floating point operation
        val actualEmploymentRate: Double = actualNumEmployee / availableEmployee
        scholarPopData.commonPopData.employmentRate = when {
            (actualEmploymentRate > 1.0) && (actualEmploymentRate < 1.01) -> 1.0
            (actualEmploymentRate > -0.01) && (actualEmploymentRate < 0.0) -> 0.0
            else -> actualEmploymentRate
        }
    }


    fun updateEngineerEmployment(
        engineerPopData: MutableEngineerPopData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
    ) {
        val salaryPerEmployee: Double =
            engineerPopData.commonPopData.salaryPerEmployee(mutableGeneralPopSystemData)

        val incomeTax: Double =
            mutableEconomyData.taxData.taxRateData.incomeTax.getIncomeTax(salaryPerEmployee)

        // Available population to work
        val availableEmployee: Double = engineerPopData.commonPopData.adultPopulation

        // Maximum scholar employee in laboratories
        val maxLaboratoryEmployee: Double =
            engineerPopData.laboratoryMap.values.fold(0.0) { acc, mutableLaboratoryData ->
                acc + mutableLaboratoryData.laboratoryInternalData.maxNumEmployee
            }

        // Compute fractions of employee if number of available employees is not enough
        val laboratoryEmployeeFraction: Double =
            when {
                maxLaboratoryEmployee <= availableEmployee -> {
                    1.0
                }
                (availableEmployee > 0.0) && (maxLaboratoryEmployee > 0.0) -> {
                    availableEmployee / maxLaboratoryEmployee
                }
                else -> {
                    0.0
                }
            }

        engineerPopData.laboratoryMap.values.forEach {
            val maxNumEmployee: Double = it.laboratoryInternalData.maxNumEmployee
            val newNumEmployee: Double = maxNumEmployee * laboratoryEmployeeFraction
            val pay: Double = newNumEmployee * salaryPerEmployee
            val tax: Double = pay * incomeTax
            val payWithTax: Double = pay + tax
            val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

            // Decide employee and payment based on the remaining scholar and fuel
            if (availableFuel - payWithTax >= 0.0) {
                // Update number of employee
                it.lastNumEmployee = newNumEmployee

                // Pay salary and tax here
                mutablePhysicsData.removeInternalProductionFuel(payWithTax)
                engineerPopData.commonPopData.saving += pay
                mutableEconomyData.taxData.storedFuelRestMass += tax

            } else {
                it.lastNumEmployee = 0.0
            }
        }

        // Actual number of employee, for computation of unemployment rate
        val actualNumEmployee: Double =
            engineerPopData.laboratoryMap.values.fold(0.0) { acc, mutableLaboratoryData ->
                acc + mutableLaboratoryData.lastNumEmployee
            }

        // Compute employment rate, allow a small error from floating point operation
        val actualEmploymentRate: Double = actualNumEmployee / availableEmployee
        engineerPopData.commonPopData.employmentRate = when {
            (actualEmploymentRate > 1.0) && (actualEmploymentRate < 1.01) -> 1.0
            (actualEmploymentRate > -0.01) && (actualEmploymentRate < 0.0) -> 0.0
            else -> actualEmploymentRate
        }
    }

    /**
     * Update soldier employment
     */
    fun updateSoldierEmployment(
        soldierPopData: MutableSoldierPopData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
    ) {
        val salaryPerEmployee: Double =
            soldierPopData.commonPopData.salaryPerEmployee(mutableGeneralPopSystemData)

        val incomeTax: Double =
            mutableEconomyData.taxData.taxRateData.incomeTax.getIncomeTax(salaryPerEmployee)

        val maxPay: Double = soldierPopData.commonPopData.adultPopulation * salaryPerEmployee
        val tax: Double = maxPay * incomeTax
        val maxPayWithTax: Double = maxPay + tax

        val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

        if (availableFuel >= maxPayWithTax) {
            soldierPopData.commonPopData.employmentRate = 1.0

            // Update military base employment
            soldierPopData.militaryBaseData.lastNumEmployee =
                soldierPopData.commonPopData.adultPopulation

            // Pay salary and tax here
            mutablePhysicsData.removeInternalProductionFuel(maxPayWithTax)
            soldierPopData.commonPopData.saving += maxPay
            mutableEconomyData.taxData.storedFuelRestMass += tax
        } else {
            soldierPopData.commonPopData.employmentRate = 0.0

            // Update military base employment
            soldierPopData.militaryBaseData.lastNumEmployee = 0.0
        }
    }

    /**
     * Generic salary payment, pay all or nothing
     */
    fun updateCommonEmployment(
        commonPopData: MutableCommonPopData,
        mutableGeneralPopSystemData: MutableGeneralPopSystemData,
        mutablePhysicsData: MutablePhysicsData,
        mutableEconomyData: MutableEconomyData,
    ) {
        val salaryPerEmployee: Double = commonPopData.salaryPerEmployee(mutableGeneralPopSystemData)

        val incomeTax: Double =
            mutableEconomyData.taxData.taxRateData.incomeTax.getIncomeTax(salaryPerEmployee)

        val maxPay: Double = commonPopData.adultPopulation * salaryPerEmployee
        val tax: Double = maxPay * incomeTax
        val maxPayWithTax: Double = maxPay + tax

        val availableFuel: Double = mutablePhysicsData.fuelRestMassData.production

        if (availableFuel >= maxPayWithTax) {
            commonPopData.employmentRate = 1.0

            // Pay salary and tax here
            mutablePhysicsData.removeInternalProductionFuel(maxPayWithTax)
            commonPopData.saving += maxPay
            mutableEconomyData.taxData.storedFuelRestMass += tax
        } else {
            commonPopData.employmentRate = 0.0
        }
    }
}