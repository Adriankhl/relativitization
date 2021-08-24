package relativitization.universe.data.subsystem.popsystem.pop

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.economy.MutableResourceQualityData
import relativitization.universe.data.subsystem.economy.ResourceQualityData
import relativitization.universe.data.subsystem.economy.ResourceType
import relativitization.universe.data.subsystem.popsystem.pop.educator.EducatorPopData
import relativitization.universe.data.subsystem.popsystem.pop.educator.MutableEducatorPopData
import relativitization.universe.data.subsystem.popsystem.pop.engineer.EngineerPopData
import relativitization.universe.data.subsystem.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.subsystem.popsystem.pop.entertainer.EntertainerPopData
import relativitization.universe.data.subsystem.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.data.subsystem.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.data.subsystem.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.subsystem.popsystem.pop.medic.MedicPopData
import relativitization.universe.data.subsystem.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.data.subsystem.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.subsystem.popsystem.pop.scholar.ScholarPopData
import relativitization.universe.data.subsystem.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.subsystem.popsystem.pop.service.ServicePopData
import relativitization.universe.data.subsystem.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.data.subsystem.popsystem.pop.soldier.SoldierPopData

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
)

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
)

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
 */
@Serializable
data class CommonPopData(
    val childPopulation: Double = 20.0,
    val adultPopulation: Double = 100.0,
    val elderlyPopulation: Double = 20.0,
    val unemploymentRate: Double = 0.0,
    val satisfaction: Double = 0.0,
    val salary: Double = 0.0,
    val unemploymentBenefit: Double = 0.0,
    val saving: Double = 1.0,
    val desireResourceMap: Map<ResourceType, ResourceQualityData> = mapOf(),
    val educationLevel: Double = 0.0,
)

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 20.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 20.0,
    var unemploymentRate: Double = 0.0,
    var satisfaction: Double = 0.0,
    var salary: Double = 0.0,
    var unemploymentBenefit: Double = 0.0,
    var saving: Double = 0.0,
    var desireResourceMap: MutableMap<ResourceType, MutableResourceQualityData> = mutableMapOf(),
    var educationLevel: Double = 0.0,
)

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


