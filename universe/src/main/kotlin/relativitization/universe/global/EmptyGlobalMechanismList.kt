package relativitization.universe.global

object EmptyGlobalMechanismList : GlobalMechanismList() {
    override val globalMechanismList: List<GlobalMechanism> = listOf()
}