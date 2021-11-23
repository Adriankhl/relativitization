package relativitization.universe.data.components.default.science.application

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.default.popsystem.CarrierInternalData
import relativitization.universe.data.components.default.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.*
import relativitization.universe.maths.algebra.Quadratic
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class ScienceApplicationData(
    val idealShip: CarrierInternalData = CarrierInternalData(),
    val idealFuelFactory: FuelFactoryInternalData = FuelFactoryInternalData(),
    val idealResourceFactoryMap: Map<ResourceType, ResourceFactoryInternalData> = mapOf(),
    val fuelLogisticsLossFractionPerDistance: Double = 0.9,
    val resourceLogisticsLossFractionPerDistance: Double = 0.9,
    val idealEntertainmentQuality: ResourceQualityData = ResourceQualityData(),
    val militaryBaseAttackFactor: Double = 1.0,
    val militaryBaseShieldFactor: Double = 1.0,
) {
    fun newFuelFactoryInternalData(
        qualityLevel: Double
    ): FuelFactoryInternalData {
        val actualQualityLevel: Double = when {
            qualityLevel > 1.0 -> {
                logger.error("quality level greater than 1.0")
                1.0
            }
            qualityLevel < 0.0 -> {
                logger.error("quality level smaller than 0.0")
                0.0
            }
            else -> {
                qualityLevel
            }
        }


        // Max increase to 5 times
        val maxOutputAmount: Double = idealFuelFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealFuelFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealFuelFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return FuelFactoryInternalData(
            maxOutputAmount = maxOutputAmount,
            maxNumEmployee = maxNumEmployee,
            size = size
        )
    }

    fun getIdealResourceFactory(resourceType: ResourceType): ResourceFactoryInternalData {
        return idealResourceFactoryMap.getOrElse(resourceType) {
            logger.debug("No ideal factory with type $resourceType")
            ResourceFactoryInternalData(outputResource = resourceType)
        }
    }

    fun newResourceFactoryInternalData(
        outputResourceType: ResourceType,
        qualityLevel: Double
    ): ResourceFactoryInternalData {
        val actualQualityLevel: Double = when {
            qualityLevel > 1.0 -> {
                logger.error("quality level greater than 1.0")
                1.0
            }
            qualityLevel < 0.0 -> {
                logger.error("quality level smaller than 0.0")
                0.0
            }
            else -> {
                qualityLevel
            }
        }

        val idealResourceFactory: ResourceFactoryInternalData =
            getIdealResourceFactory(outputResourceType)

        val maxOutputResourceQualityData: ResourceQualityData =
            idealResourceFactory.maxOutputResourceQualityData * actualQualityLevel

        // Max increase to 5 times
        val maxOutputAmount: Double = idealResourceFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        // Reduce the required input resource quality and amount
        val inputResourceMap: Map<ResourceType, InputResourceData> =
            idealResourceFactory.inputResourceMap.mapValues {
                val inputResourceData: InputResourceData = it.value

                val qualityFactor: Double = Quadratic.standard(
                    x = actualQualityLevel,
                    xMin = 0.0,
                    xMax = 1.0,
                    yMin = 0.0,
                    yMax = 1.0,
                    increasing = true,
                    accelerate = true
                )

                val amountFactor: Double = Quadratic.standard(
                    x = actualQualityLevel,
                    xMin = 0.0,
                    xMax = 1.0,
                    yMin = 0.2,
                    yMax = 1.0,
                    increasing = true,
                    accelerate = true
                )

                InputResourceData(
                    maxInputResourceQualityData = inputResourceData.maxInputResourceQualityData * qualityFactor,
                    amountPerOutputUnit = inputResourceData.amountPerOutputUnit * amountFactor
                )
            }

        // Reduce the fuel rest mass consumption rate
        val fuelRestMassConsumptionRate: Double =
            idealResourceFactory.fuelRestMassConsumptionRate * Quadratic.standard(
                x = actualQualityLevel,
                xMin = 0.0,
                xMax = 1.0,
                yMin = 0.2,
                yMax = 1.0,
                increasing = true,
                accelerate = true
            )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealResourceFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealResourceFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return ResourceFactoryInternalData(
            outputResource = outputResourceType,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = size
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutableScienceApplicationData(
    var idealShip: MutableCarrierInternalData = MutableCarrierInternalData(),
    var idealFuelFactory: MutableFuelFactoryInternalData = MutableFuelFactoryInternalData(),
    var idealResourceFactoryMap: MutableMap<ResourceType, MutableResourceFactoryInternalData> = mutableMapOf(),
    var fuelLogisticsLossFractionPerDistance: Double = 0.9,
    var resourceLogisticsLossFractionPerDistance: Double = 0.9,
    var idealEntertainmentQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var militaryBaseAttackFactor: Double = 1.0,
    var militaryBaseShieldFactor: Double = 1.0,
) {
    fun newShipInternalData(
        qualityLevel: Double
    ): MutableCarrierInternalData {
        val actualQualityLevel: Double = when {
            qualityLevel > 1.0 -> {
                logger.error("quality level greater than 1.0")
                1.0
            }
            qualityLevel < 0.0 -> {
                logger.error("quality level smaller than 0.0")
                0.0
            }
            else -> {
                qualityLevel
            }
        }


        // Reduce core rest mass
        val coreRestMass: Double = idealShip.coreRestMass * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce the number of employee needed
        val maxMovementDeltaFuelRestMass: Double =
            idealShip.maxMovementDeltaFuelRestMass * Quadratic.standard(
                x = actualQualityLevel,
                xMin = 0.0,
                xMax = 1.0,
                yMin = 0.2,
                yMax = 1.0,
                increasing = true,
                accelerate = false
            )

        // Reduce size
        val size: Double = idealShip.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce ideal population
        val idealPopulation: Double = idealShip.idealPopulation * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = false,
            accelerate = true
        )

        return MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxMovementDeltaFuelRestMass,
            size = size,
            idealPopulation = idealPopulation
        )
    }


    fun newFuelFactoryInternalData(
        qualityLevel: Double
    ): MutableFuelFactoryInternalData {
        val actualQualityLevel: Double = when {
            qualityLevel > 1.0 -> {
                logger.error("quality level greater than 1.0")
                1.0
            }
            qualityLevel < 0.0 -> {
                logger.error("quality level smaller than 0.0")
                0.0
            }
            else -> {
                qualityLevel
            }
        }


        // Max increase to 5 times
        val maxOutputAmount: Double = idealFuelFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = false
        )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealFuelFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealFuelFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return MutableFuelFactoryInternalData(
            maxOutputAmount = maxOutputAmount,
            maxNumEmployee = maxNumEmployee,
            size = size
        )
    }

    /**
     * The fuel rest mass needed to construct a new factory
     */
    fun newFuelFactoryFuelNeededByConstruction(
        qualityLevel: Double
    ): Double {
        val fuelFactoryInternalData: MutableFuelFactoryInternalData = newFuelFactoryInternalData(
            qualityLevel
        )

        return fuelFactoryInternalData.maxOutputAmount * 20
    }

    fun getIdealResourceFactory(resourceType: ResourceType): MutableResourceFactoryInternalData {
        return idealResourceFactoryMap.getOrPut(resourceType) {
            logger.debug("No ideal factory with type $resourceType")
            MutableResourceFactoryInternalData(outputResource = resourceType)
        }
    }

    fun newResourceFactoryInternalData(
        outputResourceType: ResourceType,
        qualityLevel: Double
    ): MutableResourceFactoryInternalData {
        val actualQualityLevel: Double = when {
            qualityLevel > 1.0 -> {
                logger.error("quality level greater than 1.0")
                1.0
            }
            qualityLevel < 0.0 -> {
                logger.error("quality level smaller than 0.0")
                0.0
            }
            else -> {
                qualityLevel
            }
        }

        val idealResourceFactory: MutableResourceFactoryInternalData =
            getIdealResourceFactory(outputResourceType)

        val maxOutputResourceQualityData: MutableResourceQualityData =
            idealResourceFactory.maxOutputResourceQualityData * actualQualityLevel

        // Max increase to 5 times
        val maxOutputAmount: Double = idealResourceFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        // Reduce the required input resource quality and amount
        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> =
            idealResourceFactory.inputResourceMap.mapValues {
                val inputResourceData: MutableInputResourceData = it.value

                val qualityFactor: Double = Quadratic.standard(
                    x = actualQualityLevel,
                    xMin = 0.0,
                    xMax = 1.0,
                    yMin = 0.0,
                    yMax = 1.0,
                    increasing = true,
                    accelerate = true
                )

                val amountFactor: Double = Quadratic.standard(
                    x = actualQualityLevel,
                    xMin = 0.0,
                    xMax = 1.0,
                    yMin = 0.2,
                    yMax = 1.0,
                    increasing = true,
                    accelerate = true
                )

                MutableInputResourceData(
                    maxInputResourceQualityData = inputResourceData.maxInputResourceQualityData * qualityFactor,
                    amountPerOutputUnit = inputResourceData.amountPerOutputUnit * amountFactor
                )
            }.toMutableMap()

        // Reduce the fuel rest mass consumption rate
        val fuelRestMassConsumptionRate: Double =
            idealResourceFactory.fuelRestMassConsumptionRate * Quadratic.standard(
                x = actualQualityLevel,
                xMin = 0.0,
                xMax = 1.0,
                yMin = 0.2,
                yMax = 1.0,
                increasing = true,
                accelerate = true
            )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealResourceFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealResourceFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return MutableResourceFactoryInternalData(
            outputResource = outputResourceType,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = size
        )
    }

    /**
     * The fuel rest mass needed to construct a new factory
     */
    fun newResourceFactoryFuelNeededByConstruction(
        outputResourceType: ResourceType,
        qualityLevel: Double
    ): Double {
        val resourceFactoryInternalData: MutableResourceFactoryInternalData =
            newResourceFactoryInternalData(
                outputResourceType,
                qualityLevel
            )

        return resourceFactoryInternalData.fuelRestMassConsumptionRate * 20
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}