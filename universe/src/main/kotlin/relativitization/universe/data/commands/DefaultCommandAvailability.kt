package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.events.DefaultEvent
import relativitization.universe.data.events.Event
import kotlin.reflect.KClass

@Serializable
sealed class DefaultCommand : Command()

object DefaultCommandAvailability : CommandAvailability() {
    override val commandList: List<KClass<out Command>> =
        DefaultCommand::class.sealedSubclasses

    override val addEventList: List<KClass<out Event>> = DefaultEvent::class.sealedSubclasses
}
