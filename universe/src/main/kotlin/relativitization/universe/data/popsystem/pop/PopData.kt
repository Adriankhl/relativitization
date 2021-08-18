package relativitization.universe.data.popsystem.pop

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.MutableResourceQualityData
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType
import relativitization.universe.data.popsystem.pop.educator.EducatorPopData
import relativitization.universe.data.popsystem.pop.educator.MutableEducatorPopData
import relativitization.universe.data.popsystem.pop.engineer.EngineerPopData
import relativitization.universe.data.popsystem.pop.engineer.MutableEngineerPopData
import relativitization.universe.data.popsystem.pop.entertainer.EntertainerPopData
import relativitization.universe.data.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.data.popsystem.pop.labourer.LabourerPopData
import relativitization.universe.data.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.popsystem.pop.medic.MedicPopData
import relativitization.universe.data.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.data.popsystem.pop.scholar.MutableScholarPopData
import relativitization.universe.data.popsystem.pop.scholar.ScholarPopData
import relativitization.universe.data.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.popsystem.pop.service.ServicePopData
import relativitization.universe.data.popsystem.pop.soldier.MutableSoldierPopData
import relativitization.universe.data.popsystem.pop.soldier.SoldierPopData

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

@Serializable
data class CommonPopData(
    val childPopulation: Double = 20.0,
    val adultPopulation: Double = 100.0,
    val elderlyPopulation: Double = 20.0,
    val unemploymentRate: Double = 0.0,
    val satisfaction: Double = 0.0,
    val fuelRestMassSaving: Double = 0.0,
    val desireResourceMap: Map<ResourceType, ResourceQualityData> = mapOf(),
)

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 20.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 20.0,
    var unemploymentRate: Double = 0.0,
    var satisfaction: Double = 0.0,
    var fuelRestMassSaving: Double = 0.0,
    var desireResourceMap: MutableMap<ResourceType, MutableResourceQualityData> = mutableMapOf(),
)

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


