package relativitization.universe.generate.random.science

import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.components.MutableUniverseScienceData
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableAppliedResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.data.global.components.defaults.science.knowledge.MutableProjectGenerationData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.random.Rand
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.*

object DefaultGenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Generate new universe science data based on the current universe data
     *
     * @param universeScienceData original universe data
     * @param numBasicResearchProjectGenerate number of basic research project to generate
     * @param numAppliedResearchProjectGenerate number of applied research project to generate
     * @param maxBasicReference maximum amount of reference of a project to other basic projects
     * @param maxAppliedReference maximum amount of reference of a project to other applied projects
     * @param maxDifficulty maximum difficulty of a project
     * @param maxSignificance maximum significance of a project
     */
    fun generate(
        universeScienceData: UniverseScienceData,
        numBasicResearchProjectGenerate: Int,
        numAppliedResearchProjectGenerate: Int,
        maxBasicReference: Int,
        maxAppliedReference: Int,
        maxDifficulty: Double,
        maxSignificance: Double,
    ): UniverseScienceData {

        if (numBasicResearchProjectGenerate < 0 || numAppliedResearchProjectGenerate < 0) {
            logger.error(
                "Negative numBasicResearchProjectGenerate (${numBasicResearchProjectGenerate}) or" +
                        " numAppliedResearchProjectGenerate (${numAppliedResearchProjectGenerate})"
            )
        }

        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )

        val minGenerate: Int =
            min(numBasicResearchProjectGenerate, numAppliedResearchProjectGenerate)

        if (minGenerate > 0) {
            for (i in 1..minGenerate) {
                logger.debug("Generating new basic and applied research project  .. $i")

                val newBasic: BasicResearchProjectData = generateBasicResearchProjectData(
                    mutableUniverseScienceData = mutableUniverseScienceData,
                    maxBasicReference = maxBasicReference,
                    maxAppliedReference = maxAppliedReference,
                    maxDifficulty = maxDifficulty,
                    maxSignificance = maxSignificance,
                )
                mutableUniverseScienceData.addBasicResearchProjectData(newBasic)

                val newApplied: AppliedResearchProjectData = generateAppliedResearchProjectData(
                    mutableUniverseScienceData = mutableUniverseScienceData,
                    maxBasicReference = maxBasicReference,
                    maxAppliedReference = maxAppliedReference,
                    maxDifficulty = maxDifficulty,
                    maxSignificance = maxSignificance,
                )
                mutableUniverseScienceData.addAppliedResearchProjectData(newApplied)
            }
        }

        if (numBasicResearchProjectGenerate > max(numAppliedResearchProjectGenerate, 0)) {
            for (i in 1..(numBasicResearchProjectGenerate - minGenerate)) {
                logger.debug("Generating new basic research project .. $i")

                val newBasic: BasicResearchProjectData = generateBasicResearchProjectData(
                    mutableUniverseScienceData = mutableUniverseScienceData,
                    maxBasicReference = maxBasicReference,
                    maxAppliedReference = maxAppliedReference,
                    maxDifficulty = maxDifficulty,
                    maxSignificance = maxSignificance,
                )
                mutableUniverseScienceData.addBasicResearchProjectData(newBasic)
            }
        } else if (numAppliedResearchProjectGenerate > max(numBasicResearchProjectGenerate, 0)) {
            for (i in 1..(numAppliedResearchProjectGenerate - minGenerate)) {
                logger.debug("Generating new applied research project .. $i")

                val newApplied: AppliedResearchProjectData = generateAppliedResearchProjectData(
                    mutableUniverseScienceData = mutableUniverseScienceData,
                    maxBasicReference = maxBasicReference,
                    maxAppliedReference = maxAppliedReference,
                    maxDifficulty = maxDifficulty,
                    maxSignificance = maxSignificance,
                )
                mutableUniverseScienceData.addAppliedResearchProjectData(newApplied)
            }
        }

        return DataSerializer.copy(mutableUniverseScienceData)
    }


    /**
     * Generate basic research project
     *
     * @param mutableUniverseScienceData universe science data
     * @param maxBasicReference maximum amount of reference of a project to other basic projects
     * @param maxAppliedReference maximum amount of reference of a project to other applied projects
     * @param maxDifficulty maximum difficulty of a project
     * @param maxSignificance maximum significance of a project
     */
    private fun generateBasicResearchProjectData(
        mutableUniverseScienceData: MutableUniverseScienceData,
        maxBasicReference: Int,
        maxAppliedReference: Int,
        maxDifficulty: Double,
        maxSignificance: Double,
    ): BasicResearchProjectData {
        val generationDataList: List<MutableBasicResearchProjectGenerationData> =
            mutableUniverseScienceData.universeProjectGenerationData.basicResearchProjectGenerationDataList

        val generationData: MutableBasicResearchProjectGenerationData = if (
            generationDataList.isNotEmpty()
        ) {
            WeightedReservoir.aRes(
                1,
                generationDataList
            ) {
                it.projectGenerationData.weight
            }.first()
        } else {
            logger.error("Empty basic research generation data list, default to mathematics")
            MutableBasicResearchProjectGenerationData(
                BasicResearchField.MATHEMATICS,
                MutableProjectGenerationData()
            )
        }

        val angle: Double = Rand.rand().nextDouble(0.0, PI)
        val radialDistance: Double = Rand.rand().nextDouble(
            0.0,
            generationData.projectGenerationData.range
        )

        val xCor: Double =
            generationData.projectGenerationData.centerX + radialDistance * cos(angle)
        val yCor: Double =
            generationData.projectGenerationData.centerY + radialDistance * sin(angle)

        val numReferenceBasicResearch: Int = Rand.rand().nextInt(1, maxBasicReference)
        val numReferenceAppliedResearch: Int = Rand.rand().nextInt(1, maxAppliedReference)

        val referenceBasicResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceBasicResearch,
            itemList = mutableUniverseScienceData.basicResearchProjectDataMap.keys.toList(),
        ) {
            val basicData: BasicResearchProjectData =
                mutableUniverseScienceData.basicResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, basicData.xCor, basicData.yCor)
            0.1 + 1.0 / (distance + 0.01)
        }

        val referenceAppliedResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceAppliedResearch,
            itemList = mutableUniverseScienceData.appliedResearchProjectDataMap.keys.toList(),
        ) {
            val appliedData: AppliedResearchProjectData =
                mutableUniverseScienceData.appliedResearchProjectDataMap.getValue(it)
            val distance: Double =
                Intervals.distance(xCor, yCor, appliedData.xCor, appliedData.yCor)
            0.1 + 1.0 / (distance + 0.01)
        }

        return BasicResearchProjectData(
            basicResearchId = mutableUniverseScienceData.getNewBasicResearchId(),
            basicResearchField = generationData.basicResearchField,
            xCor = xCor,
            yCor = yCor,
            difficulty = Rand.rand().nextDouble(0.0, maxDifficulty),
            significance = Rand.rand().nextDouble(0.0, maxSignificance),
            referenceBasicResearchIdList = referenceBasicResearchIdList,
            referenceAppliedResearchIdList = referenceAppliedResearchIdList,
        )
    }


    /**
     * Generate applied research project
     *
     * @param mutableUniverseScienceData universe science data
     * @param maxBasicReference maximum amount of reference of a project to other basic projects
     * @param maxAppliedReference maximum amount of reference of a project to other applied projects
     * @param maxDifficulty maximum difficulty of a project
     * @param maxSignificance maximum significance of a project
     */
    private fun generateAppliedResearchProjectData(
        mutableUniverseScienceData: MutableUniverseScienceData,
        maxBasicReference: Int,
        maxAppliedReference: Int,
        maxDifficulty: Double,
        maxSignificance: Double,
    ): AppliedResearchProjectData {
        val generationDataList: List<MutableAppliedResearchProjectGenerationData> =
            mutableUniverseScienceData.universeProjectGenerationData.appliedResearchProjectGenerationDataList

        val generationData: MutableAppliedResearchProjectGenerationData = if (
            generationDataList.isNotEmpty()
        ) {
            WeightedReservoir.aRes(
                1,
                generationDataList
            ) {
                it.projectGenerationData.weight
            }.first()
        } else {
            logger.error("Empty applied research generation data list, default to energy technology")
            MutableAppliedResearchProjectGenerationData(
                AppliedResearchField.ENERGY_TECHNOLOGY,
                MutableProjectGenerationData()
            )
        }

        val angle: Double = Rand.rand().nextDouble(0.0, PI)
        val radialDistance: Double = Rand.rand().nextDouble(
            0.0,
            generationData.projectGenerationData.range
        )

        val xCor: Double =
            generationData.projectGenerationData.centerX + radialDistance * cos(angle)
        val yCor: Double =
            generationData.projectGenerationData.centerY + radialDistance * sin(angle)

        val numReferenceBasicResearch: Int = Rand.rand().nextInt(1, maxBasicReference)
        val numReferenceAppliedResearch: Int = Rand.rand().nextInt(1, maxAppliedReference)

        val referenceBasicResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceBasicResearch,
            itemList = mutableUniverseScienceData.basicResearchProjectDataMap.keys.toList(),
        ) {
            val basicData: BasicResearchProjectData =
                mutableUniverseScienceData.basicResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, basicData.xCor, basicData.yCor)
            0.1 + 1.0 / (distance + 0.01)
        }

        val referenceAppliedResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceAppliedResearch,
            itemList = mutableUniverseScienceData.appliedResearchProjectDataMap.keys.toList(),
        ) {
            val appliedData: AppliedResearchProjectData =
                mutableUniverseScienceData.appliedResearchProjectDataMap.getValue(it)
            val distance: Double =
                Intervals.distance(xCor, yCor, appliedData.xCor, appliedData.yCor)
            0.1 + 1.0 / (distance + 0.01)
        }

        return AppliedResearchProjectData(
            appliedResearchId = mutableUniverseScienceData.getNewAppliedResearchId(),
            appliedResearchField = generationData.appliedResearchField,
            xCor = xCor,
            yCor = yCor,
            difficulty = Rand.rand().nextDouble(0.0, maxDifficulty),
            significance = Rand.rand().nextDouble(0.0, maxSignificance),
            referenceBasicResearchIdList = referenceBasicResearchIdList,
            referenceAppliedResearchIdList = referenceAppliedResearchIdList,
        )
    }
}