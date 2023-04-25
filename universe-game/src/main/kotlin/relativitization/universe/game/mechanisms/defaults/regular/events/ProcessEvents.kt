package relativitization.universe.game.mechanisms.defaults.regular.events

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.mechanisms.Mechanism
import relativitization.universe.game.utils.RelativitizationLogManager
import kotlin.random.Random

object ProcessEvents : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // Remove if the event should be canceled, before the event generate any commands
        mutablePlayerData.playerInternalData.eventDataMap.values.removeAll {
            it.event.shouldCancel(
                mutablePlayerData = mutablePlayerData,
                fromId = it.fromId,
                universeData3DAtPlayer = universeData3DAtPlayer,
                universeSettings = universeSettings
            )
        }

        // Get the command list for events which the player has make the choice
        val commandList: List<Command> = mutablePlayerData.playerInternalData.eventDataMap.values
            .filter {
                it.eventRecordData.hasChoice
            }.flatMap {
                val choiceMap: Map<Int, () -> List<Command>> = it.event.choiceAction(
                    mutablePlayerData = mutablePlayerData,
                    fromId = it.fromId,
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    universeSettings = universeSettings
                )
                if (choiceMap.containsKey(it.eventRecordData.choice)) {
                    choiceMap.getValue(it.eventRecordData.choice)()
                } else {
                    logger.error("Invalid choice ${it.eventRecordData.choice} for event $it")
                    listOf()
                }
            }

        // Remove all events with choice
        mutablePlayerData.playerInternalData.eventDataMap.values.removeAll {
            it.eventRecordData.hasChoice
        }

        // Increase stayCounter for each event
        mutablePlayerData.playerInternalData.eventDataMap.forEach {
            it.value.eventRecordData.stayCounter++
        }

        // For all outdated event, get the default choice
        val outdatedCommandList: List<Command> = mutablePlayerData.playerInternalData.eventDataMap
            .values.filter {
                it.eventRecordData.stayCounter > it.event.stayTime(it.fromId)
            }.flatMap {
                val choiceMap: Map<Int, () -> List<Command>> = it.event.choiceAction(
                    mutablePlayerData = mutablePlayerData,
                    fromId = it.fromId,
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    universeSettings = universeSettings
                )

                val defaultChoice: Int = it.event.defaultChoice(
                    mutablePlayerData = mutablePlayerData,
                    fromId = it.fromId,
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    universeSettings = universeSettings,
                    random = random,
                )

                if (choiceMap.containsKey(defaultChoice)) {
                    choiceMap.getValue(defaultChoice)()
                } else {
                    logger.error("Invalid default choice $defaultChoice for event $it")
                    listOf()
                }
            }

        // Remove all outdated event, after the command is generated from event
        mutablePlayerData.playerInternalData.eventDataMap.values.removeAll {
            it.eventRecordData.stayCounter > it.event.stayTime(it.fromId)
        }

        return commandList + outdatedCommandList
    }
}