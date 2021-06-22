package relativitization.universe.ai.default.utils

import relativitization.universe.data.MutableUniverseData3DAtPlayer
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

data class DecisionData(
    val universeData3DAtPlayer: UniverseData3DAtPlayer,
    val commandList: MutableList<Command> = mutableListOf(),
    var mutableUniverseData3DAtPlayer: MutableUniverseData3DAtPlayer = DataSerializer.copy(universeData3DAtPlayer),
) {
    fun addCommands(newCommandList: List<Command>) {
        commandList.addAll(newCommandList)
        for (command in newCommandList) {
            val playerData = mutableUniverseData3DAtPlayer.get(command.toId)
            if (playerData.id == -1) {
                logger.error("resetMutableData error: Player id -1")
            }
            command.checkAndExecute(playerData, universeData3DAtPlayer.universeSettings)
        }
    }

    fun removeCommand(commandListToRemove: List<Command>) {
        commandList.removeAll { commandListToRemove.contains(it) }
        resetMutableData()
    }

    fun resetMutableData() {
        mutableUniverseData3DAtPlayer = DataSerializer.copy(universeData3DAtPlayer)
        for (command in commandList) {
            val playerData = mutableUniverseData3DAtPlayer.get(command.toId)
            if (playerData.id == -1) {
                logger.error("resetMutableData error: Player id -1")
            }
            command.checkAndExecute(playerData, universeData3DAtPlayer.universeSettings)
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}