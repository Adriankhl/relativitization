package relativitization.universe.mechanisms

object EmptyMechanismList : MechanismList() {
    override val regularMechanismList: List<Mechanism> = listOf()

    override val dilatedMechanismList: List<Mechanism> = listOf()
}