package relativitization.universe.game.mechanisms.defaults.dilated.research

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.MutablePlayerScienceData
import relativitization.universe.game.data.components.PlayerScienceData
import relativitization.universe.game.data.components.defaults.economy.MutableResourceData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.game.data.global.components.UniverseScienceData
import relativitization.universe.game.data.global.components.universeScienceData
import relativitization.universe.game.global.defaults.science.UpdateUniverseScienceData
import relativitization.universe.core.maths.algebra.Logistic
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.math.PI
import kotlin.math.log2
import kotlin.math.min
import kotlin.random.Random

object DiscoverKnowledge : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            mutableCarrierData.allPopData.scholarPopData.instituteMap.values.forEach { mutableInstituteData ->
                updateInstituteStrength(
                    mutableCarrierData.allPopData.scholarPopData,
                    mutableInstituteData,
                    mutablePlayerData.playerInternalData.economyData().resourceData
                )

                updateBasicResearchDiscovery(
                    mutableInstituteData = mutableInstituteData,
                    mutablePlayerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                    playerScienceData = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.playerScienceData(),
                    universeScienceData = universeGlobalData.universeScienceData(),
                    random = random,
                )
            }
        }


        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            mutableCarrierData.allPopData.engineerPopData.laboratoryMap.values.forEach { mutableLaboratoryData ->
                updateLaboratoryStrength(
                    mutableCarrierData.allPopData.engineerPopData,
                    mutableLaboratoryData,
                    mutablePlayerData.playerInternalData.economyData().resourceData
                )

                updateAppliedResearchDiscovery(
                    mutableLaboratoryData = mutableLaboratoryData,
                    mutablePlayerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                    playerScienceData = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.playerScienceData(),
                    universeScienceData = universeGlobalData.universeScienceData(),
                    random = random,
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

        val basePower: Double = numEmployee * 0.5

        val equipmentAmountModifier: Double = if (numEmployee > 0.0) {
            equipmentAmount / numEmployee + 1.0
        } else {
            0.0
        }

        val baseStrength: Double = log2(1.0 + basePower * equipmentAmountModifier)

        val equipmentQualityModifier: Double = log2(2.0 + equipmentQuality.mag())

        return baseStrength * educationLevel * equipmentQualityModifier
    }

    /**
     * Update research institute strength
     */
    fun updateInstituteStrength(
        mutableScholarPopData: MutableScholarPopData,
        mutableInstituteData: MutableInstituteData,
        mutableResourceData: MutableResourceData
    ) {
        val requiredEquipmentAmount: Double =
            mutableInstituteData.instituteInternalData.researchEquipmentPerTime

        val resourceAmountMap: Map<ResourceQualityClass, Double> =
            ResourceQualityClass.values().associateWith {
                mutableResourceData.getResourceAmountData(
                    ResourceType.RESEARCH_EQUIPMENT,
                    it
                ).production
            }

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
        mutableEngineerPopData: MutableEngineerPopData,
        mutableLaboratoryData: MutableLaboratoryData,
        mutableResourceData: MutableResourceData
    ) {
        val requiredEquipmentAmount: Double =
            mutableLaboratoryData.laboratoryInternalData.researchEquipmentPerTime

        val resourceAmountMap: Map<ResourceQualityClass, Double> =
            ResourceQualityClass.values().associateWith {
                mutableResourceData.getResourceAmountData(
                    ResourceType.RESEARCH_EQUIPMENT,
                    it
                ).production
            }

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
        random: Random,
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

        val success: Boolean = random.nextDouble() < prob

        return inRange && success
    }


    /**
     * Update basic research discovery
     */
    fun updateBasicResearchDiscovery(
        mutableInstituteData: MutableInstituteData,
        mutablePlayerScienceData: MutablePlayerScienceData,
        playerScienceData: PlayerScienceData,
        universeScienceData: UniverseScienceData,
        random: Random,
    ) {
        // Done new project
        universeScienceData.basicResearchProjectDataMap.values.filter { basicResearchProjectData ->
            !mutablePlayerScienceData.doneBasicResearchProjectList.any {
                it.basicResearchId == basicResearchProjectData.basicResearchId
            }
        }.forEach { basicResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                projectXCor = basicResearchProjectData.xCor,
                projectYCor = basicResearchProjectData.yCor,
                projectDifficulty = basicResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = basicResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = basicResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableInstituteData.instituteInternalData.xCor,
                organizationYCor = mutableInstituteData.instituteInternalData.yCor,
                organizationRange = mutableInstituteData.instituteInternalData.range,
                organizationStrength = mutableInstituteData.strength,
                playerScienceData = playerScienceData,
                random = random,
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
                projectXCor = basicResearchProjectData.xCor,
                projectYCor = basicResearchProjectData.yCor,
                projectDifficulty = basicResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = basicResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = basicResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableInstituteData.instituteInternalData.xCor,
                organizationYCor = mutableInstituteData.instituteInternalData.yCor,
                organizationRange = mutableInstituteData.instituteInternalData.range,
                organizationStrength = mutableInstituteData.strength,
                playerScienceData = playerScienceData,
                strengthFactor = 4.0,
                random = random,
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
        mutableLaboratoryData: MutableLaboratoryData,
        mutablePlayerScienceData: MutablePlayerScienceData,
        playerScienceData: PlayerScienceData,
        universeScienceData: UniverseScienceData,
        random: Random,
    ) {
        // Done new project
        universeScienceData.appliedResearchProjectDataMap.values.filter { appliedResearchProjectData ->
            !mutablePlayerScienceData.doneAppliedResearchProjectList.any {
                it.appliedResearchId == appliedResearchProjectData.appliedResearchId
            }
        }.forEach { appliedResearchProjectData ->
            val success: Boolean = isResearchSuccess(
                projectXCor = appliedResearchProjectData.xCor,
                projectYCor = appliedResearchProjectData.yCor,
                projectDifficulty = appliedResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = appliedResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = appliedResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableLaboratoryData.laboratoryInternalData.xCor,
                organizationYCor = mutableLaboratoryData.laboratoryInternalData.yCor,
                organizationRange = mutableLaboratoryData.laboratoryInternalData.range,
                organizationStrength = mutableLaboratoryData.strength,
                playerScienceData = playerScienceData,
                random = random,
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
                projectXCor = appliedResearchProjectData.xCor,
                projectYCor = appliedResearchProjectData.yCor,
                projectDifficulty = appliedResearchProjectData.difficulty,
                projectReferenceBasicResearchIdList = appliedResearchProjectData.referenceBasicResearchIdList,
                projectReferenceAppliedResearchIdList = appliedResearchProjectData.referenceAppliedResearchIdList,
                organizationXCor = mutableLaboratoryData.laboratoryInternalData.xCor,
                organizationYCor = mutableLaboratoryData.laboratoryInternalData.yCor,
                organizationRange = mutableLaboratoryData.laboratoryInternalData.range,
                organizationStrength = mutableLaboratoryData.strength,
                playerScienceData = playerScienceData,
                strengthFactor = 4.0,
                random = random,
            )

            if (success) {
                mutablePlayerScienceData.knownAppliedResearchProject(
                    appliedResearchProjectData,
                )
            }
        }
    }
}