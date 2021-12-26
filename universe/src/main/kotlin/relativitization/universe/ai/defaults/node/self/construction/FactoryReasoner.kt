package relativitization.universe.ai.defaults.node.self.construction

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.ResourceType

class FactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = ResourceType.values().map {
        NewResourceFactoryReasoner(it)
    }
}

class NewResourceFactoryReasoner(
    val resourceType: ResourceType
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildNewResourceFactoryOption(resourceType),
        DoNothingDualUtilityOption(),
    )
}

class BuildNewResourceFactoryOption(
    val resourceType: ResourceType
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        TODO("Not yet implemented")
    }

    override fun getCommandList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Command> {
        TODO("Not yet implemented")
    }
}