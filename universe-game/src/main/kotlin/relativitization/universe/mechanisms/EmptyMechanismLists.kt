package relativitization.universe.mechanisms

object EmptyMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf()

    override val dilatedMechanismList: List<Mechanism> = listOf()

    override fun name(): String = "Empty"
}