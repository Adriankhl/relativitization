package relativitization.universe.global

import relativitization.universe.global.science.default.UpdateUniverseScienceData

object DefaultGlobalMechanismList : GlobalMechanismList() {
    override val globalMechanismList: List<GlobalMechanism> = listOf(
        UpdateUniverseScienceData
    )
}