package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.*
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
        numBasicResarchProjectGenerate: Int,
        numAppliedResearchProjectGenerate: Int,
    ): UniverseScienceData {
        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )

        val minGenerate: Int = min(numBasicResarchProjectGenerate, numAppliedResearchProjectGenerate)

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

        if (numBasicResarchProjectGenerate > numAppliedResearchProjectGenerate) {
            for (i in 1..(numBasicResarchProjectGenerate - minGenerate)) {
                logger.debug("Generating new basic research project .. $i")

                val newBasic: BasicResearchProjectData = generateBasicResearchProjectData(
                    mutableUniverseScienceData
                )
                mutableUniverseScienceData.addBasicResearchProjectData(newBasic)
            }
        } else if (numAppliedResearchProjectGenerate > numBasicResarchProjectGenerate) {
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

        val angle: Double = Random.Default.nextDouble(0.0, PI)
        val radialDistance: Double = Random.Default.nextDouble(
            0.0,
            generationData.projectGenerationData.range
        )

        val xCor: Double = generationData.projectGenerationData.centerX + radialDistance * cos(angle)
        val yCor: Double = generationData.projectGenerationData.centerY + radialDistance * sin(angle)

        val numReferenceBasicResearch: Int = Random.Default.nextInt(1, 10)
        val numReferenceAppliedResearch: Int = Random.Default.nextInt(1, 10)

        val referenceBasicResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceBasicResearch,
            itemList = mutableUniverseScienceData.basicResearchProjectDataMap.keys.toList(),
        ) {
            val basicData: BasicResearchProjectData = mutableUniverseScienceData.basicResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, basicData.xCor, basicData.yCor)
            0.1 + 1.0 / distance
        }


        val referenceAppliedResearchIdList: List<Int> = WeightedReservoir.aRes(
            numItem = numReferenceAppliedResearch,
            itemList = mutableUniverseScienceData.appliedResearchProjectDataMap.keys.toList(),
        ) {
            val appliedData: AppliedResearchProjectData = mutableUniverseScienceData.appliedResearchProjectDataMap.getValue(it)
            val distance: Double = Intervals.distance(xCor, yCor, appliedData.xCor, appliedData.yCor)
            0.1 + 1.0 / distance
        }

        return BasicResearchProjectData(
            basicResearchId = mutableUniverseScienceData.getNewBasicResearchId(),
            basicResearchField = generationData.basicResearchField,
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.Default.nextDouble(0.0, 1.0),
            significance = Random.Default.nextDouble(0.0, 1.0),
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
        TODO()
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
            KnowledgeField.PHYSICS -> generatePhysicsKnowledge(mutableUniverseScienceData)
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
        val technologyFieldGenerate: TechnologyField = WeightedReservoir.aRes(
            numItem = 1,
            itemList = mutableUniverseScienceData.technologyGenerationData.generationDataMap.keys.toList(),
        ) {
            mutableUniverseScienceData.technologyGenerationData.generationDataMap.getValue(
                it
            ).weight
        }.first()


        return when(technologyFieldGenerate) {
            TechnologyField.MAX_SHIP_REST_MASS -> generateMaxShipRestMassTechnology(mutableUniverseScienceData)
            else -> {
                logger.error("Can't generate knowledge field ${technologyFieldGenerate}, " +
                        "default to max ship rest mass")
                generateMaxShipRestMassTechnology(mutableUniverseScienceData)
            }
        }
    }

    /**
     * Generate mathematics knowledge
     */
    private fun generateMathematicsKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MathematicsKnowledge {

        logger.debug("Generating mathematics knowledge")

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
     * Generate physics knowledge
     */
    private fun generatePhysicsKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): PhysicsKnowledge {

        logger.debug("Generating physics knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.PHYSICS
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

        return PhysicsKnowledge(
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
     * Generate material knowledge
     */
    private fun generateMaterialKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MaterialKnowledge {

        logger.debug("Generating material knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.MATERIAL
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

        return MaterialKnowledge(
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
     * Generate computer knowledge
     */
    private fun generateComputerKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): ComputerKnowledge {

        logger.debug("Generating computer knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.COMPUTER
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

        return ComputerKnowledge(
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
     * Generate mechanics knowledge
     */
    private fun generateMechanicsKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MechanicsKnowledge {

        logger.debug("Generating mechanics knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.MECHANICS
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

        return MechanicsKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate medicine knowledge
     */
    private fun generateMedicineKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): MedicineKnowledge {

        logger.debug("Generating medicine knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.MEDICINE
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

        return MedicineKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate economy knowledge
     */
    private fun generateEconomyKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): EconomyKnowledge {

        logger.debug("Generating economy knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.ECONOMY
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

        return EconomyKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate politics knowledge
     */
    private fun generatePoliticsKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): PoliticsKnowledge {

        logger.debug("Generating politics knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.POLITICS
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

        return PoliticsKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate sociology knowledge
     */
    private fun generateSociologyKnowledge(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): SociologyKnowledge {

        logger.debug("Generating sociology knowledge")

        val generationData: MutableKnowledgeFieldGenerationData =
            mutableUniverseScienceData.knowledgeGenerationData.generationDataMap.getValue(
                KnowledgeField.MECHANICS
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

        return SociologyKnowledge(
            knowledgeId = mutableUniverseScienceData.getNewKnowledgeId(),
            importance = Random.nextDouble(0.0, 1.0),
            xCor = xCor,
            yCor = yCor,
            difficulty = Random.nextDouble(0.0, 1.0),
            referenceKnowledgeIdList = referenceKnowledgeIdList,
            referenceTechnologyIdList = referenceTechnologyIdList
        )
    }

    /**
     * Generate max rest mass ship technology
     */
    private fun generateMaxShipRestMassTechnology(
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