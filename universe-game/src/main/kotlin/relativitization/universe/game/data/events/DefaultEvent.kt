package relativitization.universe.game.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.events.Event

@Serializable
sealed class DefaultEvent : Event()
