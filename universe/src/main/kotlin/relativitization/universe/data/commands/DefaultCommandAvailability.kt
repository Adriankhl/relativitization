package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.events.DefaultEvent
import relativitization.universe.data.events.name

@Serializable
sealed class DefaultCommand : Command()

object DefaultCommandAvailability : CommandAvailability() {
    override val commandList: List<String> = DefaultCommand::class.sealedSubclasses.map { it.name() }

    override val addEventList: List<String> = DefaultEvent::class.sealedSubclasses.map { it.name() }
}
