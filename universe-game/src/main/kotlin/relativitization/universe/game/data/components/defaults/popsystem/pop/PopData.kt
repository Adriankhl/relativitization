package relativitization.universe.game.data.components.defaults.popsystem.pop

import kotlinx.serialization.Serializable
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.GeneralPopSystemData
import relativitization.universe.game.data.components.defaults.popsystem.MutableGeneralPopSystemData
import relativitization.universe.game.data.components.defaults.popsystem.pop.educator.EducatorPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.educator.MutableEducatorPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.EngineerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.entertainer.EntertainerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.medic.MedicPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.ScholarPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.MutableServicePopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.ServicePopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.soldier.SoldierPopData
import relativitization.universe.core.utils.RelativitizationLogManager

/**
 * For events and commands specifically for a given type of pop
 */
enum class PopType(val value: String) {
    LABOURER("Labourer"),
    SCHOLAR("Scholar"),
    ENGINEER("Engineer"),
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
    fun getCommonPopData(popType: PopType): CommonPopData = when (popType) {
        PopType.LABOURER -> labourerPopData.commonPopData
        PopType.SCHOLAR -> scholarPopData.commonPopData
        PopType.ENGINEER -> engineerPopData.commonPopData
        PopType.EDUCATOR -> educatorPopData.commonPopData
        PopType.MEDIC -> medicPopData.commonPopData
        PopType.SERVICE_WORKER -> servicePopData.commonPopData
        PopType.ENTERTAINER -> entertainerPopData.commonPopData
        PopType.SOLDIER -> soldierPopData.commonPopData
    }

    fun totalAdultPopulation(): Double = PopType.values().fold(0.0) { acc, popType ->
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
    fun getCommonPopData(popType: PopType): MutableCommonPopData = when (popType) {
        PopType.LABOURER -> labourerPopData.commonPopData
        PopType.SCHOLAR -> scholarPopData.commonPopData
        PopType.ENGINEER -> engineerPopData.commonPopData
        PopType.EDUCATOR -> educatorPopData.commonPopData
        PopType.MEDIC -> medicPopData.commonPopData
        PopType.SERVICE_WORKER -> servicePopData.commonPopData
        PopType.ENTERTAINER -> entertainerPopData.commonPopData
        PopType.SOLDIER -> soldierPopData.commonPopData
    }

    fun addDesireResource(
        popType: PopType,
        resourceType: ResourceType,
        resourceQualityData: ResourceQualityData,
        resourceAmount: Double
    ) {
        val commonPopData: MutableCommonPopData = getCommonPopData(popType)

        commonPopData.addDesireResource(
            resourceType,
            resourceQualityData,
            resourceAmount
        )
    }

    fun totalAdultPopulation(): Double = PopType.values().fold(0.0) { acc, popType ->
        acc + getCommonPopData(popType).adultPopulation
    }
}

/**
 * Common data for pop
 *
 * @property childPopulation amount of child population
 * @property adultPopulation amount of adult population
 * @property elderlyPopulation amount of elderly population
 * @property educationLevel the education level of the population
 * @property employmentRate rate of employed adult
 * @property satisfaction how satisfy is the population
 * @property saving saving of the population in fuel rest mass
 * @property salaryFactor multiply this with the base salary to get the actual salary
 * @property desireResourceMap the desire resources of the population
 * @property resourceInputMap store the resource input to this population to fulfill the desire,
 *  should be cleared after calculating the effect of the input
 * @property lastResourceInputMap store the latest resource input just before it is cleared
 */
@Serializable
data class CommonPopData(
    val childPopulation: Double = 0.0,
    val adultPopulation: Double = 100.0,
    val elderlyPopulation: Double = 0.0,
    val educationLevel: Double = 1.0,
    val employmentRate: Double = 1.0,
    val satisfaction: Double = 0.0,
    val salaryFactor: Double = 1.0,
    val saving: Double = 0.0,
    val desireResourceMap: Map<ResourceType, ResourceDesireData> = mapOf(),
    val resourceInputMap: Map<ResourceType, ResourceDesireData> = mapOf(),
    val lastResourceInputMap: Map<ResourceType, ResourceDesireData> = mapOf(),
) {
    /**
     * Compute the salary per employee of this pop
     */
    fun salaryPerEmployee(generalPopSystemData: GeneralPopSystemData): Double {
        return generalPopSystemData.baseSalaryPerEmployee * salaryFactor
    }
}

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 0.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 0.0,
    var educationLevel: Double = 1.0,
    var employmentRate: Double = 1.0,
    var satisfaction: Double = 0.0,
    var salaryFactor: Double = 1.0,
    var saving: Double = 0.0,
    var desireResourceMap: MutableMap<ResourceType, MutableResourceDesireData> = mutableMapOf(),
    var resourceInputMap: MutableMap<ResourceType, MutableResourceDesireData> = mutableMapOf(),
    var lastResourceInputMap: MutableMap<ResourceType, MutableResourceDesireData> = mutableMapOf(),
) {
    fun salaryPerEmployee(generalPopSystemData: MutableGeneralPopSystemData): Double {
        return generalPopSystemData.baseSalaryPerEmployee * salaryFactor
    }

    fun numEmployee(): Double = when {
        employmentRate > 1.0 -> {
            logger.error("Employment rate > 1.0")
            adultPopulation
        }
        employmentRate < 0.0 -> {
            logger.error("Employment rate < 0.0")
            0.0
        }
        else -> {
            adultPopulation * employmentRate
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
        val desireData: MutableResourceDesireData =
            resourceInputMap.getOrPut(
                resourceType
            ) {
                MutableResourceDesireData()
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
        val desireData: MutableResourceDesireData =
            resourceInputMap.getOrPut(
                resourceType
            ) {
                MutableResourceDesireData()
            }

        val originalAmount: Double = desireData.desireAmount

        desireData.desireQuality.updateQuality(
            originalAmount = originalAmount,
            newAmount = resourceAmount,
            newData = resourceQualityData,
        )

        desireData.desireAmount += resourceAmount
    }

    fun addAdultPopulation(
        otherPopulation: Double,
        otherEducationLevel: Double,
        otherSatisfaction: Double,
        otherSaving: Double
    ) {
        val totalAdultPopulation: Double = adultPopulation + otherPopulation
        if (totalAdultPopulation > 0.0) {
            educationLevel = (adultPopulation * educationLevel +
                    otherPopulation * otherEducationLevel) / totalAdultPopulation
            satisfaction = (adultPopulation * satisfaction +
                    otherPopulation * otherSatisfaction) / totalAdultPopulation
            saving += otherSaving
            adultPopulation = totalAdultPopulation
        }
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


