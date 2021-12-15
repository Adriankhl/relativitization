package relativitization.universe.mechanisms.defaults.regular.events

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

        // Remove if the event should be canceled, before the event generate any commands
        mutablePlayerData.playerInternalData.eventDataMap.filter { (eventId, mutableEventData) ->
            mutableEventData.event.shouldCancelThisEvent(
                eventId,
                mutableEventData.eventRecordData,
                universeData3DAtPlayer
            )
        }.keys.forEach {
            mutablePlayerData.playerInternalData.eventDataMap.remove(it)
        }

        // Get the command list
        val commandList: List<Command> =
            mutablePlayerData.playerInternalData.eventDataMap.map { (eventId, mutableEventData) ->
                if (mutableEventData.eventRecordData.hasChoice) {
                    mutableEventData.event.generateCommands(
                        eventId,
                        mutableEventData.eventRecordData,
                        universeData3DAtPlayer
                    )
                } else {
                    // change the choice if there is choice from player
                    mutableEventData.eventRecordData.choice =
                        mutableEventData.event.defaultChoice(
                            eventId,
                            mutableEventData.eventRecordData,
                            universeData3DAtPlayer
                        )

                    mutableEventData.event.generateCommands(
                        eventId,
                        mutableEventData.eventRecordData,
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
            it.checkAndSelfExecuteBeforeSend(mutablePlayerData, universeData3DAtPlayer.universeSettings)
            it.checkAndExecute(mutablePlayerData, universeData3DAtPlayer.universeSettings)
        }

        // Self execute other commands
        otherCommandList.forEach {
            it.checkAndSelfExecuteBeforeSend(mutablePlayerData, universeData3DAtPlayer.universeSettings)
        }

        // Increase stayCounter for each event
        mutablePlayerData.playerInternalData.eventDataMap.forEach {
            it.value.eventRecordData.stayCounter++
        }


        // Remove all outdated event, after the command is generated from event
        mutablePlayerData.playerInternalData.eventDataMap.filter {
            it.value.eventRecordData.stayCounter > it.value.event.stayTime
        }.keys.forEach {
            mutablePlayerData.playerInternalData.eventDataMap.remove(it)
        }
        return otherCommandList
    }
}