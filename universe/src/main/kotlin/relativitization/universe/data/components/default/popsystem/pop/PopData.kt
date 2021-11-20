package relativitization.universe.data.components.default.popsystem.pop

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.popsystem.pop.educator.EducatorPopData
import relativitization.universe.data.components.popsystem.pop.educator.MutableEducatorPopData
import relativitization.universe.data.components.popsystem.pop.engineer.EngineerPopData
import relativitization.universe.data.components.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.components.popsystem.pop.entertainer.EntertainerPopData
import relativitization.universe.data.components.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.data.components.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.data.components.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.components.popsystem.pop.medic.MedicPopData
import relativitization.universe.data.components.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.data.components.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.components.popsystem.pop.scholar.ScholarPopData
import relativitization.universe.data.components.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.components.popsystem.pop.service.ServicePopData
import relativitization.universe.data.components.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.data.components.popsystem.pop.soldier.SoldierPopData
import relativitization.universe.utils.RelativitizationLogManager

/**
 * For events and commands specifically for a given type of pop
 */
enum class PopType(val value: String) {
    LABOURER("Labourer"),
    ENGINEER("Engineer"),
    SCHOLAR("Scholar"),
    EDUCATOR("Educator"),
    MEDIC("Medic"),
    SERVICE_WORKER("Service worker"),
    ENTERTAINER("Entertainer"),
    SOLDIER("Soldier"),
    ;

    override fun toString(): String {
        return value
    }
}

/**
 * Store all pop data in carrier
 */
@Serializable
data class AllPopData(
    val labourerPopData: LabourerPopData = LabourerPopData(),
    val engineerPopData: EngineerPopData = EngineerPopData(),
    val scholarPopData: ScholarPopData = ScholarPopData(),
    val educatorPopData: EducatorPopData = EducatorPopData(),
    val medicPopData: MedicPopData = MedicPopData(),
    val servicePopData: ServicePopData = ServicePopData(),
    val entertainerPopData: EntertainerPopData = EntertainerPopData(),
    val soldierPopData: SoldierPopData = SoldierPopData(),
) {
    fun getCommonPopData(popType: relativitization.universe.data.components.default.popsystem.pop.PopType): relativitization.universe.data.components.default.popsystem.pop.CommonPopData = when(popType) {
        relativitization.universe.data.components.default.popsystem.pop.PopType.LABOURER -> labourerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.ENGINEER -> engineerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SCHOLAR -> scholarPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.EDUCATOR -> educatorPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.MEDIC -> medicPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SERVICE_WORKER -> servicePopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.ENTERTAINER -> entertainerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SOLDIER -> soldierPopData.commonPopData
    }

    fun totalAdultPopulation(): Double = relativitization.universe.data.components.default.popsystem.pop.PopType.values().fold(0.0) { acc, popType ->
        acc + getCommonPopData(popType).adultPopulation
    }
}

@Serializable
data class MutableAllPopData(
    var labourerPopData: MutableLabourerPopData = MutableLabourerPopData(),
    var engineerPopData: MutableEngineerPopData = MutableEngineerPopData(),
    var scholarPopData: MutableScholarPopData = MutableScholarPopData(),
    var educatorPopData: MutableEducatorPopData = MutableEducatorPopData(),
    var medicPopData: MutableMedicPopData = MutableMedicPopData(),
    var servicePopData: MutableServicePopData = MutableServicePopData(),
    var entertainerPopData: MutableEntertainerPopData = MutableEntertainerPopData(),
    var soldierPopData: MutableSoldierPopData = MutableSoldierPopData(),
) {
    fun getCommonPopData(popType: relativitization.universe.data.components.default.popsystem.pop.PopType): relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = when(popType) {
        relativitization.universe.data.components.default.popsystem.pop.PopType.LABOURER -> labourerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.ENGINEER -> engineerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SCHOLAR -> scholarPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.EDUCATOR -> educatorPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.MEDIC -> medicPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SERVICE_WORKER -> servicePopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.ENTERTAINER -> entertainerPopData.commonPopData
        relativitization.universe.data.components.default.popsystem.pop.PopType.SOLDIER -> soldierPopData.commonPopData
    }

    fun addResource(
        popType: relativitization.universe.data.components.default.popsystem.pop.PopType,
        resourceType: ResourceType,
        resourceQualityData: ResourceQualityData,
        resourceAmount: Double
    ) {
        val commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = getCommonPopData(popType)

        commonPopData.addDesireResource(
            resourceType,
            resourceQualityData,
            resourceAmount
        )
    }

    fun totalAdultPopulation(): Double = relativitization.universe.data.components.default.popsystem.pop.PopType.values().fold(0.0) { acc, popType ->
        acc + getCommonPopData(popType).adultPopulation
    }
}

/**
 * Common data for pop
 *
 * @property childPopulation amount of child population
 * @property adultPopulation amount of adult population
 * @property elderlyPopulation amount of elderly population
 * @property unemploymentRate rate of unemployed adult
 * @property satisfaction how satisfy is the population
 * @property salary the total amount of salary per turn of the employed population
 * @property unemploymentBenefit the total amount of the unemployment benefit of the unemployed population
 * @property saving saving of the population in fuel rest mass
 * @property desireResourceMap the desire resources of the population
 * @property educationLevel the education level of the population
 * @property resourceInputMap the resource input to this population to fulfill the desire
 */
@Serializable
data class CommonPopData(
    val childPopulation: Double = 0.0,
    val adultPopulation: Double = 100.0,
    val elderlyPopulation: Double = 0.0,
    val unemploymentRate: Double = 0.0,
    val satisfaction: Double = 0.0,
    val salary: Double = 0.0,
    val unemploymentBenefit: Double = 0.0,
    val saving: Double = 1.0,
    val desireResourceMap: Map<ResourceType, relativitization.universe.data.components.default.popsystem.pop.ResourceDesireData> = mapOf(),
    val educationLevel: Double = 1.0,
    val resourceInputMap: Map<ResourceType, relativitization.universe.data.components.default.popsystem.pop.ResourceDesireData> = mapOf(),
)

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 0.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 0.0,
    var unemploymentRate: Double = 0.0,
    var satisfaction: Double = 0.0,
    var salary: Double = 0.0,
    var unemploymentBenefit: Double = 0.0,
    var saving: Double = 0.0,
    var desireResourceMap: MutableMap<ResourceType, relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData> = mutableMapOf(),
    var educationLevel: Double = 1.0,
    var resourceInputMap: MutableMap<ResourceType, relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData> = mutableMapOf(),
) {
    fun numEmployee(): Double = when {
        unemploymentRate > 1.0 -> {
            relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData.Companion.logger.error("Unemployment rate > 1.0")
            0.0
        }
        unemploymentRate < 0.0 -> {
            relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData.Companion.logger.error("Unemployment rate < 0.0")
            adultPopulation
        }
        else -> {
            adultPopulation * (1 - unemploymentRate)
        }
    }

    /**
     * Add resource to lastDesireResourceMap
     */
    fun addDesireResource(
        resourceType: ResourceType,
        resourceQualityData: ResourceQualityData,
        resourceAmount: Double,
    ) {
        val desireData: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData = resourceInputMap.getOrPut(
            resourceType
        ) {
            relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
        }

        val originalAmount: Double = desireData.desireAmount

        desireData.desireQuality.updateQuality(
            originalAmount = originalAmount,
            newAmount = resourceAmount,
            newData = resourceQualityData.toMutableResourceQualityData(),
        )

        desireData.desireAmount += resourceAmount

    }


    /**
     * Add resource to lastDesireResourceMap for mutable resource quality data
     */
    fun addDesireResource(
        resourceType: ResourceType,
        resourceQualityData: MutableResourceQualityData,
        resourceAmount: Double,
    ) {
        val desireData: relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData = resourceInputMap.getOrPut(
            resourceType
        ) {
            relativitization.universe.data.components.default.popsystem.pop.MutableResourceDesireData()
        }

        val originalAmount: Double = desireData.desireAmount

        desireData.desireQuality.updateQuality(
            originalAmount = originalAmount,
            newAmount = resourceAmount,
            newData = resourceQualityData,
        )

        desireData.desireAmount += resourceAmount

    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * A single desired resource of a pop
 *
 * @property desireAmount the amount desired in one turn
 * @property desireQuality the desired quality of the resource
 */
@Serializable
data class ResourceDesireData(
    val desireAmount: Double = 0.0,
    val desireQuality: ResourceQualityData = ResourceQualityData(),
)

@Serializable
data class MutableResourceDesireData(
    var desireAmount: Double = 0.0,
    var desireQuality: MutableResourceQualityData = MutableResourceQualityData(),
)


