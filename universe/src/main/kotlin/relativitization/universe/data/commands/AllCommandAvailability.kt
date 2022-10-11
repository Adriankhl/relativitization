package relativitization.universe.data.commands

import relativitization.universe.data.events.Event
import kotlin.reflect.KClass

object AllCommandAvailability : CommandAvailability() {
    override val commandList: List<KClass<out Command>> = listOf()

    override val addEventList: List<KClass<out Event>> = listOf()

    override fun name(): String = "All"
}