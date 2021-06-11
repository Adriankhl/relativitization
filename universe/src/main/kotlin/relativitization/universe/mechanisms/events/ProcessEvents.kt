package relativitization.universe.mechanisms.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.mechanisms.Mechanism

object ProcessEvents : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData
    ): List<Command> {
        // Remove all outdated event
        mutablePlayerData.playerInternalData.eventDataList.removeAll { mutableEventData ->
            mutableEventData.stayCounter > mutableEventData.event.stayTime
        }

        // Remove if the event should be canceled
        val cancelEventList: List<MutableEventData> = mutablePlayerData.playerInternalData.eventDataList.filter { mutableEventData ->
            mutableEventData.event.shouldCancelThisEvent(mutableEventData, universeData3DAtPlayer)
        }
        mutablePlayerData.playerInternalData.eventDataList.removeAll(cancelEventList)

        // Get the command list
        val commandList: List<Command> = mutablePlayerData.playerInternalData.eventDataList.map { mutableEventData ->
            if (mutableEventData.hasChoice) {
                mutableEventData.event.generateCommands(mutableEventData.choice, universeData3DAtPlayer)
            } else {
                mutableEventData.event.generateCommands(
                    mutableEventData.event.defaultChoice(universeData3DAtPlayer),
                    universeData3DAtPlayer
                )
            }
        }.flatten()

        // Separate self commands and other commands

        // Increase stayCounter for each event
        mutablePlayerData.playerInternalData.eventDataList.forEach { mutableEventData ->
            mutableEventData.stayCounter ++
        }
        return commandList
    }
}