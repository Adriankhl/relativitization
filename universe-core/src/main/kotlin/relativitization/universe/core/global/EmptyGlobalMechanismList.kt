package relativitization.universe.core.global

object EmptyGlobalMechanismList : GlobalMechanismList() {
    override val globalMechanismList: List<GlobalMechanism> = listOf()

    override fun name(): String = "Empty"
}