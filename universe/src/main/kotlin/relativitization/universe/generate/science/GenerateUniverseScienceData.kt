package relativitization.universe.generate.science

import relativitization.universe.data.components.default.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchProjectData
import relativitization.universe.data.components.default.science.knowledge.BasicResearchField
import relativitization.universe.data.components.default.science.knowledge.BasicResearchProjectData
import relativitization.universe.data.global.components.MutableUniverseScienceData
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.global.components.default.science.knowledge.MutableAppliedResearchProjectGenerationData
import relativitization.universe.data.global.components.default.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.data.global.components.default.science.knowledge.MutableProjectGenerationData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.*
import kotlin.random.Random

object DefaultGenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Generate new universe science data based on the current universe data
     */
    fun generate(
        universeScienceData: UniverseScienceData,
        numBasicResearchProjectGenerate: Int,
        numAppliedResearchProjectGenerate: Int,
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
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addBasicResearchProjectData(newBasic)

                val newApplied: AppliedResearchProjectData = generateAppliedResearchProjectData(
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addAppliedResearchProjectData(newApplied)
            }
        }

        if (numBasicResearchProjectGenerate > max(numAppliedResearchProjectGenerate, 0)) {
            for (i in 1..(numBasicResearchProjectGenerate - minGenerate)) {
                logger.debug("Generating new basic research project .. $i")

                val newBasic: BasicResearchProjectData = generateBasicResearchProjectData(
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addBasicResearchProjectData(newBasic)
            }
        } else if (numAppliedResearchProjectGenerate > max(numBasicResearchProjectGenerate, 0)) {
            for (i in 1..(numAppliedResearchProjectGenerate - minGenerate)) {
                logger.debug("Generating new applied research project .. $i")

                val newApplied: AppliedResearchProjectData = generateAppliedResearchProjectData(
                    mutableUniverseScienceData
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
     */
    private fun generateBasicResearchProjectData(
        mutableUniverseScienceData: MutableUniverseScienceData,
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

        val angle: Double = Random.nextDouble(0.0, PI)
        val radialDistance: Double = Random.nextDouble(
            0.0,
            generationData.projectGenerationData.range
        )

        val xCor: Double =
            generationData.projectGenerationData.centerX + radialDistance * cos(angle)
        val yCor: Double =
            generationData.projectGenerationData.centerY + radialDistance * sin(angle)

        val numReferenceBasicResearch: Int = Random.nextInt(1, 10)
        val numReferenceAppliedResearch: Int = Random.nextInt(1, 10)

        val referenceBasicResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceBasicResearch,
            itemList = mutableUniverseScienceData.basicResearchProjectDataMap.keys.toList(),
        ) {
            val basicData: BasicResearchProjectData =
                mutableUniverseScienceData.basicResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, basicData.xCor, basicData.yCor)
            0.1 + 1.0 / distance
        }

        val referenceAppliedResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceAppliedResearch,
            itemList = mutableUniverseScienceData.appliedResearchProjectDataMap.keys.toList(),
        ) {
            val appliedData: AppliedResearchProjectData =
                mutableUniverseScienceData.appliedResearchProjectDataMap.getValue(it)
            val distance: Double =
                Intervals.distance(xCor, yCor, appliedData.xCor, appliedData.yCor)
            0.1 + 1.0 / distance
        }

        return BasicResearchProjectData(
            basicResearchId = mutableUniverseScienceData.getNewBasicResearchId(),
            basicResearchField = generationData.basicResearchField,
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            significance = Random.nextDouble(0.0, 1.0),
            referenceBasicResearchIdList = referenceBasicResearchIdList,
            referenceAppliedResearchIdList = referenceAppliedResearchIdList,
        )
    }


    /**
     * Generate applied research project
     *
     * @param mutableUniverseScienceData universe science data
     */
    private fun generateAppliedResearchProjectData(
        mutableUniverseScienceData: MutableUniverseScienceData,
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

        val angle: Double = Random.nextDouble(0.0, PI)
        val radialDistance: Double = Random.nextDouble(
            0.0,
            generationData.projectGenerationData.range
        )

        val xCor: Double =
            generationData.projectGenerationData.centerX + radialDistance * cos(angle)
        val yCor: Double =
            generationData.projectGenerationData.centerY + radialDistance * sin(angle)

        val numReferenceBasicResearch: Int = Random.nextInt(1, 10)
        val numReferenceAppliedResearch: Int = Random.nextInt(1, 10)

        val referenceBasicResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceBasicResearch,
            itemList = mutableUniverseScienceData.basicResearchProjectDataMap.keys.toList(),
        ) {
            val basicData: BasicResearchProjectData =
                mutableUniverseScienceData.basicResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, basicData.xCor, basicData.yCor)
            0.1 + 1.0 / distance
        }

        val referenceAppliedResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceAppliedResearch,
            itemList = mutableUniverseScienceData.appliedResearchProjectDataMap.keys.toList(),
        ) {
            val appliedData: AppliedResearchProjectData =
                mutableUniverseScienceData.appliedResearchProjectDataMap.getValue(it)
            val distance: Double =
                Intervals.distance(xCor, yCor, appliedData.xCor, appliedData.yCor)
            0.1 + 1.0 / distance
        }

        return AppliedResearchProjectData(
            appliedResearchId = mutableUniverseScienceData.getNewAppliedResearchId(),
            appliedResearchField = generationData.appliedResearchField,
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            significance = Random.nextDouble(0.0, 1.0),
            referenceBasicResearchIdList = referenceBasicResearchIdList,
            referenceAppliedResearchIdList = referenceAppliedResearchIdList,
        )
    }
}