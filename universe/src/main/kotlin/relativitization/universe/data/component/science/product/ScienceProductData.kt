package relativitization.universe.data.component.science.product

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableInputResourceData
import relativitization.universe.maths.algebra.Quadratic
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class ScienceProductData(
    val maxShipRestMass: Double = 10000.0,
    val maxShipEnginePowerByRestMass: Double = 1E-6,
    val idealFactoryMap: Map<ResourceType, FactoryInternalData> = mapOf(),
    val fuelLogisticsLossFractionPerDistance: Double = 0.9,
    val resourceLogisticsLossFractionPerDistance: Double = 0.9,
) {
    fun getIdealFactory(resourceType: ResourceType): FactoryInternalData {
        return idealFactoryMap.getOrElse(resourceType) {
            logger.debug("No ideal factory with type $resourceType")
            FactoryInternalData(outputResource = resourceType)
        }
    }

    fun newFactoryInternalData(
        outputResourceType: ResourceType,
        qualityLevel: Double
    ): FactoryInternalData {
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

        val idealFactory: FactoryInternalData = getIdealFactory(outputResourceType)

        val maxOutputResourceQualityData: ResourceQualityData = idealFactory.maxOutputResourceQualityData * actualQualityLevel

        // Max increase to 5 times
        val maxOutputAmount: Double = idealFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        // Reduce the required input resource quality and amount
        val inputResourceMap: Map<ResourceType, InputResourceData> = idealFactory.inputResourceMap.mapValues {
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
        val fuelRestMassConsumptionRate: Double = idealFactory.fuelRestMassConsumptionRate * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return FactoryInternalData(
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
data class MutableScienceProductData(
    var maxShipRestMass: Double = 10000.0,
    var maxShipEnginePowerByRestMass: Double = 1E-6,
    var idealFactoryMap: MutableMap<ResourceType, MutableFactoryInternalData> = mutableMapOf(),
    var fuelLogisticsLossFractionPerDistance: Double = 0.9,
    var resourceLogisticsLossFractionPerDistance: Double = 0.9,
) {
    fun getIdealFactory(resourceType: ResourceType): MutableFactoryInternalData {
        return idealFactoryMap.getOrPut(resourceType) {
            logger.debug("No ideal factory with type $resourceType")
            MutableFactoryInternalData(outputResource = resourceType)
        }
    }

    fun newFactoryInternalData(
        outputResourceType: ResourceType,
        qualityLevel: Double
    ): MutableFactoryInternalData {
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

        val idealFactory: MutableFactoryInternalData = getIdealFactory(outputResourceType)

        val maxOutputResourceQualityData: MutableResourceQualityData = idealFactory.maxOutputResourceQualityData * actualQualityLevel

        // Max increase to 5 times
        val maxOutputAmount: Double = idealFactory.maxOutputAmount * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        // Reduce the required input resource quality and amount
        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = idealFactory.inputResourceMap.mapValues {
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
        val fuelRestMassConsumptionRate: Double = idealFactory.fuelRestMassConsumptionRate * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce the number of employee needed
        val maxNumEmployee: Double = idealFactory.maxNumEmployee * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        // Reduce size
        val size: Double = idealFactory.size * Quadratic.standard(
            x = actualQualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 0.2,
            yMax = 1.0,
            increasing = true,
            accelerate = true
        )

        return MutableFactoryInternalData(
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