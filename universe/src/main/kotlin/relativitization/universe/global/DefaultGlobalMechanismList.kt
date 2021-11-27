package relativitization.universe.global

import relativitization.universe.global.defaults.science.UpdateUniverseScienceData

object DefaultGlobalMechanismList : GlobalMechanismList() {
    override val globalMechanismList: List<GlobalMechanism> = listOf(
        UpdateUniverseScienceData
    )
}