package relativitization.universe.mechanisms.defaults.research

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.PlayerScienceData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.maths.algebra.Logistic
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.PI
import kotlin.math.log2
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

object DiscoverKnowledge : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

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

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            mutableCarrierData.allPopData.scholarPopData.instituteMap.values.forEach { mutableInstituteData ->
                updateInstituteStrength(
                    gamma,
                    mutableCarrierData.allPopData.scholarPopData,
                    mutableInstituteData,
                    mutablePlayerData.playerInternalData.economyData().resourceData
                )

                updateBasicResearchDiscovery(
                    gamma = gamma,
                    mutableInstituteData = mutableInstituteData,
                    mutablePlayerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                    playerScienceData = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.playerScienceData(),
                    universeScienceData = universeGlobalData.universeScienceData(),
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

                updateAppliedResearchDiscovery(
                    gamma = gamma,
                    mutableLaboratoryData = mutableLaboratoryData,
                    mutablePlayerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                    playerScienceData = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.playerScienceData(),
                    universeScienceData = universeGlobalData.universeScienceData(),
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

        val humanPower: Double = log2(numEmployee + 1.0) * educationLevel

        val machinePower: Double = log2(equipmentAmount + 1.0) * equipmentQuality.mag()

        return (humanPower + machinePower) * 0.5
    }

    /**
     * Update research institute strength
     */
    fun updateInstituteStrength(
        gamma: Double,
        mutableScholarPopData: MutableScholarPopData,
        mutableInstituteData: MutableInstituteData,
        mutableResourceData: MutableResourceData
    ) {
        val requiredEquipmentAmount: Double = mutableInstituteData.researchEquipmentPerTime * gamma

        val resourceAmountMap: Map<ResourceQualityClass, Double> =
            ResourceQualityClass.values().map {
                it to mutableResourceData.getResourceAmountData(
                    ResourceType.RESEARCH_EQUIPMENT,
                    it
                ).production
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

        val equipmentAmount: Double =
            min(requiredEquipmentAmount, resourceAmountMap.getValue(resourceQualityClass))

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

        val resourceAmountMap: Map<ResourceQualityClass, Double> =
            ResourceQualityClass.values().map {
                it to mutableResourceData.getResourceAmountData(
                    ResourceType.RESEARCH_EQUIPMENT,
                    it
                ).production
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

        val equipmentAmount: Double =
            min(requiredEquipmentAmount, resourceAmountMap.getValue(resourceQualityClass))

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

    /**
     * Whether this project can be discovered
     */
    fun isResearchSuccess(
        gamma: Double,
        projectXCor: Double,
        projectYCor: Double,
        projectDifficulty: Double,
        projectReferenceBasicResearchIdList: List<Int>,
        projectReferenceAppliedResearchIdList: List<Int>,
        organizationXCor: Double,
        organizationYCor: Double,
        organizationRange: Double,
        organizationStrength: Double,
        playerScienceData: PlayerScienceData,
        strengthFactor: Double = 1.0,
    ): Boolean {

        // Minimum range is 0.25
        val area: Double = if (organizationRange >= 0.25) {
            organizationRange * organizationRange * PI
        } else {
            logger.error("Institute range smaller than 0.25")
            0.25 * 0.25 * PI
        }

        val averageStrength: Double = organizationStrength / area

        val inRange: Boolean = Intervals.distance(
            projectXCor,
            projectYCor,
            organizationXCor,
            organizationYCor
        ) <= organizationRange

        // More done related research, higher this factor
        val doneReferenceFactor: Double =
            if (projectReferenceBasicResearchIdList.isNotEmpty() ||
                projectReferenceAppliedResearchIdList.isNotEmpty()
            ) {
                val numBasic: Int = projectReferenceBasicResearchIdList.filter { id ->
                    playerScienceData.doneBasicResearchProjectList.any {
                        it.basicResearchId == id
                    }
                }.size

                val numApplied: Int = projectReferenceAppliedResearchIdList.filter { id ->
                    playerScienceData.doneAppliedResearchProjectList.any {
                        it.appliedResearchId == id
                    }
                }.size

                val total: Int =
                    projectReferenceBasicResearchIdList.size + projectReferenceAppliedResearchIdList.size

                val factor: Double = (numBasic + numApplied).toDouble() / total.toDouble()

                // Minimum factor to 0.01, there is a chance to discover this even if no reference is there
                if (factor > 0.01) {
                    factor
                } else {
                    0.01
                }
            } else {
                1.0
            }

        val actualStrength: Double = averageStrength * doneReferenceFactor * strengthFactor

        // Probability of successfully complete the project
        val prob: Double = if (actualStrength > 0.0) {
            Logistic.standardLogistic(log2(actualStrength / projectDifficulty))
        } else {
            0.0
        }

        // Probability adjusted by time dilation
        val actualProb: Double = 1.0 - (1.0 - prob).pow(1.0 / gamma)

        // The probability is affected by time dilation, i.e. gamma
        val success: Boolean = Random.nextDouble() < actualProb

        return inRange && success
    }


    /**
     * Update basic research discovery
     */
    fun updateBasicResearchDiscovery(
        gamma: Double,
        mutableInstituteData: MutableInstituteData,
        mutablePlayerScienceData: MutablePlayerScienceData,
        playerScienceData: PlayerScienceData,
        universeScienceData: UniverseScienceData,
    ) {
        // Done new project
        universeScienceData.basicResearchProjectDataMap.values.filter { basicResearchProjectData ->
            !mutablePlayerScienceData.doneBasicResearchProjectList.any {
                it.basicResearchId == basicResearchProjectData.basicResearchId
            }
        }.forEach { basicResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                gamma = gamma,
                projectXCor = basicResearchProjectData.xCor,
                projectYCor = basicResearchProjectData.yCor,
                projectDifficulty = basicResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = basicResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = basicResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableInstituteData.xCor,
                organizationYCor = mutableInstituteData.yCor,
                organizationRange = mutableInstituteData.range,
                organizationStrength = mutableInstituteData.strength,
                playerScienceData = playerScienceData,
            )

            if (success) {
                mutablePlayerScienceData.doneBasicResearchProject(
                    basicResearchProjectData,
                    UpdateUniverseScienceData.basicResearchProjectFunction()
                )
            }
        }

        // Know new project
        universeScienceData.basicResearchProjectDataMap.values.filter { basicResearchProjectData ->

            val allProject: List<BasicResearchProjectData> =
                mutablePlayerScienceData.doneBasicResearchProjectList + mutablePlayerScienceData.knownBasicResearchProjectList

            !allProject.any {
                it.basicResearchId == basicResearchProjectData.basicResearchId
            }
        }.forEach { basicResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                gamma = gamma,
                projectXCor = basicResearchProjectData.xCor,
                projectYCor = basicResearchProjectData.yCor,
                projectDifficulty = basicResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = basicResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = basicResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableInstituteData.xCor,
                organizationYCor = mutableInstituteData.yCor,
                organizationRange = mutableInstituteData.range,
                organizationStrength = mutableInstituteData.strength,
                playerScienceData = playerScienceData,
                strengthFactor = 4.0,
            )

            if (success) {
                mutablePlayerScienceData.knownBasicResearchProject(
                    basicResearchProjectData,
                )
            }
        }
    }


    /**
     * Update basic research discovery
     */
    fun updateAppliedResearchDiscovery(
        gamma: Double,
        mutableLaboratoryData: MutableLaboratoryData,
        mutablePlayerScienceData: MutablePlayerScienceData,
        playerScienceData: PlayerScienceData,
        universeScienceData: UniverseScienceData,
    ) {
        // Done new project
        universeScienceData.appliedResearchProjectDataMap.values.filter { appliedResearchProjectData ->
            !mutablePlayerScienceData.doneAppliedResearchProjectList.any {
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }
        }.forEach { appliedResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                gamma = gamma,
                projectXCor = appliedResearchProjectData.xCor,
                projectYCor = appliedResearchProjectData.yCor,
                projectDifficulty = appliedResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = appliedResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = appliedResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableLaboratoryData.xCor,
                organizationYCor = mutableLaboratoryData.yCor,
                organizationRange = mutableLaboratoryData.range,
                organizationStrength = mutableLaboratoryData.strength,
                playerScienceData = playerScienceData,
            )

            if (success) {
                mutablePlayerScienceData.doneAppliedResearchProject(
                    appliedResearchProjectData,
                    UpdateUniverseScienceData.appliedResearchProjectFunction()
                )
            }
        }

        // Know new project
        universeScienceData.appliedResearchProjectDataMap.values.filter { appliedResearchProjectData ->

            val allProject: List<AppliedResearchProjectData> =
                mutablePlayerScienceData.doneAppliedResearchProjectList + mutablePlayerScienceData.knownAppliedResearchProjectList

            !allProject.any {
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }
        }.forEach { appliedResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                gamma = gamma,
                projectXCor = appliedResearchProjectData.xCor,
                projectYCor = appliedResearchProjectData.yCor,
                projectDifficulty = appliedResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = appliedResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = appliedResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableLaboratoryData.xCor,
                organizationYCor = mutableLaboratoryData.yCor,
                organizationRange = mutableLaboratoryData.range,
                organizationStrength = mutableLaboratoryData.strength,
                playerScienceData = playerScienceData,
                strengthFactor = 4.0,
            )

            if (success) {
                mutablePlayerScienceData.knownAppliedResearchProject(
                    appliedResearchProjectData,
                )
            }
        }
    }
}