package relativitization.universe.mechanisms

object EmptyMechanismList : MechanismList() {
    override val mechanismList: List<Mechanism> = listOf()
}