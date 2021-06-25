package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable

@Serializable
data class AllPopData(
    val labourerPopData: LabourerPopData = LabourerPopData(),
    val engineerPopData: EngineerPopData = EngineerPopData(),
    val scholarPopData: ScholarPopData = ScholarPopData(),
    val educatorPopData: EducatorPopData = EducatorPopData(),
)

@Serializable
data class MutableAllPopData(
    var labourerPopData: MutableLabourerPopData = MutableLabourerPopData(),
    var engineerPopData: MutableEngineerPopData = MutableEngineerPopData(),
    var scholarPopData: MutableScholarPopData = MutableScholarPopData(),
    var educatorPopData: MutableEducatorPopData = MutableEducatorPopData(),
)

@Serializable
data class CommonPopData(
    val childPopulation: Double = 20.0,
    val adultPopulation: Double = 100.0,
    val elderlyPopulation: Double = 20.0,
    val satisfaction: Double = 0.0,
    val fuelRestMassSaving: Double = 0.0,
)

@Serializable
data class MutableCommonPopData(
    var childPopulation: Double = 20.0,
    var adultPopulation: Double = 100.0,
    var elderlyPopulation: Double = 20.0,
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
data class EducatorPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEducatorPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)