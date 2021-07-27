package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.data.science.technology.MaxShipRestMassTechnology
import relativitization.universe.data.science.technology.MutableTechnologyFieldGenerationData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.science.technology.TechnologyField
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

object DefaultGenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Generate new universe science data based on the current universe data
     */
    fun generate(
        universeData: UniverseData,
        numKnowledgeGenerate: Int,
        numTechnologyGenerate: Int,
    ): UniverseScienceData {
        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )

        val minGenerate: Int = min(numKnowledgeGenerate, numTechnologyGenerate)

        for (i in 1..minGenerate) {
            logger.debug("Generating new knowledge and technology .. $i")

            val newKnowledgeData: SingleKnowledgeData = generateSingleKnowledgeData(
                mutableUniverseScienceData
            )
            mutableUniverseScienceData.addSingleKnowledgeData(newKnowledgeData)

            val newTechnologyData: SingleTechnologyData = generateSingleTechnologyData(
                mutableUniverseScienceData
            )
            mutableUniverseScienceData.addSingleTechnologyData(newTechnologyData)
        }

        if (numKnowledgeGenerate > numTechnologyGenerate) {
            for (i in 1..(numKnowledgeGenerate - minGenerate)) {
                logger.debug("Generating new knowledge .. $i")
                val newKnowledgeData: SingleKnowledgeData = generateSingleKnowledgeData(
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addSingleKnowledgeData(newKnowledgeData)
            }
        } else if (numTechnologyGenerate > numKnowledgeGenerate) {
            for (i in 1..(numTechnologyGenerate - minGenerate)) {
                logger.debug("Generating new technology .. $i")
                val newTechnologyData: SingleTechnologyData = generateSingleTechnologyData(
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addSingleTechnologyData(newTechnologyData)
            }
        }

        return DataSerializer.copy(mutableUniverseScienceData)
    }

    /**
     * Generate Single knowledge data
     *
     * @param mutableUniverseScienceData universe science data
     */
    private fun generateSingleKnowledgeData(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): SingleKnowledgeData {
        val knowledgeFieldGenerate: KnowledgeField = WeightedReservoir.aRes(
            numItem = 1,
            itemList = mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.keys.toList(),
        ) {
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                it
            ).weight
        }.first()

        return when(knowledgeFieldGenerate) {
            KnowledgeField.MATHEMATICS -> generateMathematicsKnowledge(mutableUniverseScienceData)
            else -> {
                logger.error("Can't generate knowledge field ${knowledgeFieldGenerate}, " +
                        "default to mathematics")
                generateMathematicsKnowledge(mutableUniverseScienceData)
            }
        }
    }

    /**
     * Generate single technology data
     *
     * @param mutableUniverseScienceData universe science data
     */
    private fun generateSingleTechnologyData(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): SingleTechnologyData {
        TODO()
    }

    /**
     * Generate mathematics knowledge
     */
    private fun generateMathematicsKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MathematicsKnowledge {

        logger.debug("Generating Mathematics knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.MATHEMATICS
            )

        val angle: Double = Random.Default.nextDouble(0.0, PI)
        val radialDistance: Double = Random.Default.nextDouble(0.0, generationData.range)

        val xCor: Double = generationData.centerX + radialDistance * cos(angle)
        val yCor: Double = generationData.centerY + radialDistance * sin(angle)

        val numReferenceKnowledge: Int = Random.Default.nextInt(1, 10)
        val numReferenceTechnology: Int = Random.Default.nextInt(1, 10)

        val referenceKnowledgeIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceKnowledge,
            itemList = mutableUniverseScienceData.allSingleKnowledgeDataMap.keys.toList(),
        ) {
            val knowledgeData: SingleKnowledgeData = mutableUniverseScienceData.allSingleKnowledgeDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, knowledgeData.xCor, knowledgeData.yCor)
            0.1 + 1.0 / distance
        }

        val referenceTechnologyIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceTechnology,
            itemList = mutableUniverseScienceData.allSingleTechnologyDataMap.keys.toList(),
        ) {
            val technologyData: SingleTechnologyData = mutableUniverseScienceData.allSingleTechnologyDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, technologyData.xCor, technologyData.yCor)
            0.1 + 1.0 / distance
        }

        return MathematicsKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.Default.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.Default.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate max rest mass ship technology
     */
    fun generateMaxShipRestMassTechnology(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MaxShipRestMassTechnology {
        logger.debug("Generating max rest mass ship technology")

        val generationData: MutableTechnologyFieldGenerationData =
            mutableUniverseScienceData.technologyGenerationData.generationDataMap.getValue(
                TechnologyField.MAX_SHIP_REST_MASS
            )

        val angle: Double = Random.Default.nextDouble(0.0, PI)
        val radialDistance: Double = Random.Default.nextDouble(0.0, generationData.range)

        val xCor: Double = generationData.centerX + radialDistance * cos(angle)
        val yCor: Double = generationData.centerY + radialDistance * sin(angle)

        val numReferenceKnowledge: Int = Random.Default.nextInt(1, 10)
        val numReferenceTechnology: Int = Random.Default.nextInt(1, 10)

        val referenceKnowledgeIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceKnowledge,
            itemList = mutableUniverseScienceData.allSingleKnowledgeDataMap.keys.toList(),
        ) {
            val knowledgeData: SingleKnowledgeData = mutableUniverseScienceData.allSingleKnowledgeDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, knowledgeData.xCor, knowledgeData.yCor)
            0.1 + 1.0 / distance
        }

        val referenceTechnologyIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceTechnology,
            itemList = mutableUniverseScienceData.allSingleTechnologyDataMap.keys.toList(),
        ) {
            val technologyData: SingleTechnologyData = mutableUniverseScienceData.allSingleTechnologyDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, technologyData.xCor, technologyData.yCor)
            0.1 + 1.0 / distance
        }

        return MaxShipRestMassTechnology(
            technologyId = mutableUniverseScienceData.getNewTechnologyId(),
            maxShipRestMassIncrease = Random.Default.nextDouble(0.2E6, 1.0E6),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.Default.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList,
        )
    }
}