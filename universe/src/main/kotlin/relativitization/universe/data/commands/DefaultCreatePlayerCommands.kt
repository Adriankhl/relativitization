package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Split carrier to create new player
 *
 * @property carrierIdList the id of the carriers to form the new player
 * @property resourceFraction the fraction of fuel and resource from original player to new player
 */
@Serializable
data class SplitCarrierCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val carrierIdList: List<Int>,
    val resourceFraction: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Create new player with carriers: "),
            IntString(0),
            NormalString(". ")
        ),
        listOf(
            carrierIdList.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,

            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val isCarrierIdValid = CommandErrorMessage(
            carrierIdList.all {
                playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(it)
            },
            I18NString("Invalid carrier id. ")
        )

        val isResourceFractionValid = CommandErrorMessage(
            (resourceFraction >= 0.0) && (resourceFraction <= 1.0),
            I18NString("Invalid resource fraction. ")
        )

        val isCarrierListNotEmpty = CommandErrorMessage(
            carrierIdList.isNotEmpty(),
            I18NString("Carrier list is empty. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                isCarrierIdValid,
                isResourceFractionValid,
                isCarrierListNotEmpty,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val newPlayerInternalData: MutablePlayerInternalData = MutablePlayerInternalData(
            directLeaderId = playerData.playerId,
            leaderIdList = (playerData.playerInternalData.leaderIdList + playerData.playerId).toMutableList(),
        )

        // copy ai data
        val newAIData: MutableAIData = DataSerializer.copy(playerData.playerInternalData.aiData())
        newPlayerInternalData.aiData(newAIData)

        // copy diplomacy data and remove war state
        val newDiplomacyData: MutableDiplomacyData =
            DataSerializer.copy(playerData.playerInternalData.diplomacyData())
        newDiplomacyData.warData.warStateMap.clear()
        newPlayerInternalData.diplomacyData(newDiplomacyData)

        // split the economy data to new player
        val newEconomyData: MutableEconomyData =
            DataSerializer.copy(playerData.playerInternalData.economyData())
        newEconomyData.resourceData.singleResourceMap.forEach { (_, qualityMap) ->
            qualityMap.forEach { (_, singleResourceData) ->
                singleResourceData.resourceAmount.storage *= resourceFraction
                singleResourceData.resourceAmount.production *= resourceFraction
                singleResourceData.resourceAmount.trade *= resourceFraction
            }
        }
        newPlayerInternalData.economyData(newEconomyData)

        // reduce original resource
        playerData.playerInternalData.economyData().resourceData.singleResourceMap.forEach { (_, qualityMap) ->
            qualityMap.forEach { (_, singleResourceData) ->
                singleResourceData.resourceAmount.storage *= (1.0 - resourceFraction)
                singleResourceData.resourceAmount.production *= (1.0 - resourceFraction)
                singleResourceData.resourceAmount.trade *= (1.0 - resourceFraction)
            }
        }

        // Use default modifier data
        val newModifierData: MutableModifierData = MutableModifierData()
        newPlayerInternalData.modifierData(newModifierData)

        // split fuel rest mass data
        val newPhysicsData: MutablePhysicsData =
            DataSerializer.copy(playerData.playerInternalData.physicsData())
        newPhysicsData.fuelRestMassData.movement *= resourceFraction
        newPhysicsData.fuelRestMassData.trade *= resourceFraction
        newPhysicsData.fuelRestMassData.production *= resourceFraction
        newPlayerInternalData.physicsData(newPhysicsData)

        // reduce original fuel
        playerData.playerInternalData.physicsData().fuelRestMassData.movement *= (1.0 - resourceFraction)
        playerData.playerInternalData.physicsData().fuelRestMassData.trade *= (1.0 - resourceFraction)
        playerData.playerInternalData.physicsData().fuelRestMassData.production *= (1.0 - resourceFraction)

        // Copy science data
        val newPlayerScienceData: MutablePlayerScienceData =
            DataSerializer.copy(playerData.playerInternalData.playerScienceData())
        newPlayerInternalData.playerScienceData(newPlayerScienceData)

        // Copy politics data
        val newPoliticsData: MutablePoliticsData =
            DataSerializer.copy(playerData.playerInternalData.politicsData())
        newPlayerInternalData.politicsData(newPoliticsData)

        // Split carrier
        val newPopSystemData: MutablePopSystemData =
            DataSerializer.copy(playerData.playerInternalData.popSystemData())
        val toRemoveCarrierId: List<Int> = newPopSystemData.carrierDataMap.keys.filter {
            !carrierIdList.contains(it)
        }
        toRemoveCarrierId.forEach { newPopSystemData.carrierDataMap.remove(it) }
        newPlayerInternalData.popSystemData(newPopSystemData)

        val toRemoveOriginalCarrierId: List<Int> =
            playerData.playerInternalData.popSystemData().carrierDataMap.keys.filter {
                carrierIdList.contains(it)
            }
        toRemoveOriginalCarrierId.forEach {
            playerData.playerInternalData.popSystemData().carrierDataMap.remove(it)
        }

        // Sync data and add the new player internal data to new player list
        playerData.syncData()
        newPlayerInternalData.syncDataComponent()
        playerData.newPlayerList.add(newPlayerInternalData)
    }
}

/**
 * Grant independence to direct subordinate, if the sender is not a top leader, the player
 * still belong to the leader of one level higher
 */
@Serializable
data class GrantIndependenceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Grant independence to "),
            IntString(0),
            NormalString(". "),
        ),
        listOf(
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectSubordinate = CommandErrorMessage(
            playerData.isDirectSubOrdinate(toId),
            CommandI18NStringFactory.isNotDirectSubordinate(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isDirectSubordinate
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectLeader = CommandErrorMessage(
            playerData.playerInternalData.directLeaderId == fromId,
            CommandI18NStringFactory.isNotDirectLeader(playerData.playerId, fromId),
        )

        return CommandErrorMessage(
            listOf(
                isDirectLeader
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val newLeaderIdList: List<Int> = playerData.playerInternalData.leaderIdList -
                playerData.playerInternalData.directLeaderId
        playerData.changeDirectLeaderId(newLeaderIdList)
    }
}