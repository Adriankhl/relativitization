package relativitization.universe.game.global

import relativitization.universe.game.global.defaults.science.UpdateUniverseScienceData

object DefaultGlobalMechanismList : GlobalMechanismList() {
    override val globalMechanismList: List<GlobalMechanism> = listOf(
        UpdateUniverseScienceData
    )

    override fun name(): String = "Default"
}