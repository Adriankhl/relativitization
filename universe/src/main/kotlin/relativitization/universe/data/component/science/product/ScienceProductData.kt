package relativitization.universe.data.component.science.product

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.InputResourceData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryInternalData
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
        val actualAdvancement: Double = when {
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

        val maxOutputResourceQualityData: ResourceQualityData = idealFactory.maxOutputResourceQualityData * actualAdvancement

        // Max increase to 5 times
        val maxOutputAmount: Double = idealFactory.maxOutputAmount * Quadratic.standard(
            x = qualityLevel,
            xMin = 0.0,
            xMax = 1.0,
            yMin = 1.0,
            yMax = 5.0,
            increasing = false,
            accelerate = true
        )

        val inputResourceMap: Map<ResourceType, InputResourceData> = idealFactory.inputResourceMap.mapValues {
            val inputResourceData: InputResourceData = it.value

            it.value
        }

        return FactoryInternalData(
            outputResource = outputResourceType,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = mapOf(),
            fuelRestMassConsumptionRate = 0.0,
            maxNumEmployee = 0.0,
            size = 0.0
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
)