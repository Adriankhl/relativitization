package relativitization.universe.mechanisms.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.economy.MutableResourceData
import relativitization.universe.data.components.economy.ResourceQualityClass
import relativitization.universe.data.components.economy.ResourceQualityData
import relativitization.universe.data.components.economy.ResourceType
import relativitization.universe.data.components.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.components.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.data.components.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.components.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.log2
import kotlin.math.min

object DiscoverKnowledge : Mechanism() {
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

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            mutableCarrierData.allPopData.scholarPopData.instituteMap.values.forEach { mutableInstituteData ->
                updateInstituteStrength(
                    gamma,
                    mutableCarrierData.allPopData.scholarPopData,
                    mutableInstituteData,
                    mutablePlayerData.playerInternalData.economyData().resourceData
                )
            }
        }


        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            mutableCarrierData.allPopData.engineerPopData.laboratoryMap.values.forEach { mutableLaboratoryData ->
                updateLaboratoryStrength(
                    gamma,
                    mutableCarrierData.allPopData.engineerPopData,
                    mutableLaboratoryData,
                    mutablePlayerData.playerInternalData.economyData().resourceData
                )
            }
        }

        return listOf()
    }

    fun computeStrength(
        numEmployee: Double,
        educationLevel: Double,
        equipmentAmount: Double,
        equipmentQuality: ResourceQualityData
    ): Double {

        val humanPower: Double = log2(numEmployee) * educationLevel

        val machinePower: Double = log2(equipmentAmount) * equipmentQuality.mag()

        return (humanPower + machinePower) * 0.5
    }

    /**
     * Update research institute strength
     */ fun updateInstituteStrength( gamma: Double,
        mutableScholarPopData: MutableScholarPopData,
        mutableInstituteData: MutableInstituteData,
        mutableResourceData: MutableResourceData
    ) {
        val requiredEquipmentAmount: Double = mutableInstituteData.researchEquipmentPerTime * gamma

        val resourceAmountMap: Map<ResourceQualityClass, Double> = ResourceQualityClass.values().map {
            it to mutableResourceData.getResourceAmountData(ResourceType.RESEARCH_EQUIPMENT, it).production
        }.toMap()

        val resourceQualityClass: ResourceQualityClass =
            if (resourceAmountMap.values.any { it >= requiredEquipmentAmount }) {
                resourceAmountMap.filter {
                    it.value >= requiredEquipmentAmount
                }.firstNotNullOfOrNull {
                    it.key
                } ?: ResourceQualityClass.FIRST
            } else {
                resourceAmountMap.maxByOrNull { it.value }?.key ?: ResourceQualityClass.FIRST
            }

        val equipmentAmount: Double = min(requiredEquipmentAmount, resourceAmountMap.getValue(resourceQualityClass))

        mutableResourceData.getResourceAmountData(
            ResourceType.RESEARCH_EQUIPMENT,
            resourceQualityClass
        ).production -= equipmentAmount

        mutableInstituteData.strength = computeStrength(
            numEmployee = mutableInstituteData.lastNumEmployee,
            educationLevel = mutableScholarPopData.commonPopData.educationLevel,
            equipmentAmount = equipmentAmount,
            equipmentQuality = mutableResourceData.getResourceQuality(
                ResourceType.RESEARCH_EQUIPMENT,
                resourceQualityClass
            ).toResourceQualityData()
        )
    }


    /**
     * Update laboratory strength
     */
    fun updateLaboratoryStrength(
        gamma: Double,
        mutableEngineerPopData: MutableEngineerPopData,
        mutableLaboratoryData: MutableLaboratoryData,
        mutableResourceData: MutableResourceData
    ) {
        val requiredEquipmentAmount: Double = mutableLaboratoryData.researchEquipmentPerTime * gamma

        val resourceAmountMap: Map<ResourceQualityClass, Double> = ResourceQualityClass.values().map {
            it to mutableResourceData.getResourceAmountData(ResourceType.RESEARCH_EQUIPMENT, it).production
        }.toMap()

        val resourceQualityClass: ResourceQualityClass =
            if (resourceAmountMap.values.any { it >= requiredEquipmentAmount }) {
                resourceAmountMap.filter {
                    it.value >= requiredEquipmentAmount
                }.firstNotNullOfOrNull {
                    it.key
                } ?: ResourceQualityClass.FIRST
            } else {
                resourceAmountMap.maxByOrNull { it.value }?.key ?: ResourceQualityClass.FIRST
            }

        val equipmentAmount: Double = min(requiredEquipmentAmount, resourceAmountMap.getValue(resourceQualityClass))

        mutableResourceData.getResourceAmountData(
            ResourceType.RESEARCH_EQUIPMENT,
            resourceQualityClass
        ).production -= equipmentAmount

        mutableLaboratoryData.strength = computeStrength(
            numEmployee = mutableLaboratoryData.lastNumEmployee,
            educationLevel = mutableEngineerPopData.commonPopData.educationLevel,
            equipmentAmount = equipmentAmount,
            equipmentQuality = mutableResourceData.getResourceQuality(
                ResourceType.RESEARCH_EQUIPMENT,
                resourceQualityClass
            ).toResourceQualityData()
        )
    }
}