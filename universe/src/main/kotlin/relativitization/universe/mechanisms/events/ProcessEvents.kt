package relativitization.universe.mechanisms.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object ProcessEvents : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Remove all outdated event
        mutablePlayerData.playerInternalData.eventDataMap.filter {
            it.value.stayCounter > it.value.event.stayTime
        }.keys.forEach {
            mutablePlayerData.playerInternalData.eventDataMap.remove(it)
        }

        // Remove if the event should be canceled
        mutablePlayerData.playerInternalData.eventDataMap.filter {
            it.value.event.shouldCancelThisEvent(it.value, universeData3DAtPlayer)
        }.keys.forEach {
            mutablePlayerData.playerInternalData.eventDataMap.remove(it)
        }

        // Get the command list
        val commandList: List<Command> =
            mutablePlayerData.playerInternalData.eventDataMap.values.map { mutableEventData ->
                if (mutableEventData.hasChoice) {
                    mutableEventData.event.generateCommands(
                        mutableEventData.choice,
                        universeData3DAtPlayer
                    )
                } else {
                    mutableEventData.event.generateCommands(
                        mutableEventData.event.defaultChoice(universeData3DAtPlayer),
                        universeData3DAtPlayer
                    )
                }
            }.flatten()

        // Separate self commands and other commands
        val (selfCommandList, otherCommandList) = commandList.partition {
            it.toId == mutablePlayerData.playerId
        }

        // Execute self commands
        selfCommandList.forEach {
            it.checkAndExecute(mutablePlayerData, universeData3DAtPlayer.universeSettings)
        }

        // Increase stayCounter for each event
        mutablePlayerData.playerInternalData.eventDataMap.forEach {
            it.value.stayCounter++
        }
        return otherCommandList
    }
}