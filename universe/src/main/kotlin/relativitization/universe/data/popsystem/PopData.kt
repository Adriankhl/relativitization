package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable

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
    val serviceWorkerPopData: ServiceWorkerPopData = ServiceWorkerPopData(),
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
    var serviceWorkerPopData: MutableServiceWorkerPopData = MutableServiceWorkerPopData(),
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
)

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 20.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 20.0,
    var unemploymentRate: Double = 0.0,
    var satisfaction: Double = 0.0,
    var fuelRestMassSaving: Double = 0.0,
)

@Serializable
data class LabourerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class EngineerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEngineerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class ScholarPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableScholarPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class MedicPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableMedicPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)


@Serializable
data class EducatorPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEducatorPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class ServiceWorkerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableServiceWorkerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class EntertainerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEntertainerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)

@Serializable
data class SoldierPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableSoldierPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)