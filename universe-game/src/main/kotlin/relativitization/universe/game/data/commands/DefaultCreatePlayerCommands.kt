package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.game.ai.DefaultAI
import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.MutablePlayerInternalData
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.components.MutableAIData
import relativitization.universe.game.data.components.MutableDiplomacyData
import relativitization.universe.game.data.components.MutableEconomyData
import relativitization.universe.game.data.components.MutableModifierData
import relativitization.universe.game.data.components.MutablePhysicsData
import relativitization.universe.game.data.components.MutablePlayerScienceData
import relativitization.universe.game.data.components.MutablePoliticsData
import relativitization.universe.game.data.components.MutablePopSystemData
import relativitization.universe.game.data.components.aiData
import relativitization.universe.game.data.components.defaults.economy.MutableSingleResourceData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.politicsData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.components.syncData
import relativitization.universe.game.data.components.syncDataComponent
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.maths.physics.Int4D
import relativitization.universe.game.utils.I18NString
import relativitization.universe.game.utils.IntString
import relativitization.universe.game.utils.NormalString

/**
 * Split carrier to create new player
 *
 * @property carrierIdList the id of the carriers to form the new player
 * @property storageFraction the fraction of fuel and resource from original player storage to
 *  new player
 */
@Serializable
data class SplitCarrierCommand(
    override val toId: Int,
    val carrierIdList: List<Int>,
    val storageFraction: Double,
) : DefaultCommand() {
    override fun name(): String = "Split Carrier"

    override fun description(fromId: Int): I18NString = I18NString(
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
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val isCarrierIdValid = CommandErrorMessage(
            carrierIdList.all {
                playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(it)
            },
            I18NString("Invalid carrier id. ")
        )

        val isCarrierListNotEmpty = CommandErrorMessage(
            carrierIdList.isNotEmpty(),
            I18NString("Carrier list is empty. ")
        )

        val isRemainingCarrierNonZero = CommandErrorMessage(
            (playerData.playerInternalData.popSystemData().carrierDataMap.keys - carrierIdList.toSet()).isNotEmpty(),
            I18NString("Zero remaining carrier. ")
        )

        val isStorageFractionValid = CommandErrorMessage(
            (storageFraction >= 0.0) && (storageFraction <= 1.0),
            I18NString("Invalid storage fraction. ")
        )


        return CommandErrorMessage(
            listOf(
                isSelf,
                isCarrierIdValid,
                isCarrierListNotEmpty,
                isRemainingCarrierNonZero,
                isStorageFractionValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val newPlayerInternalData = MutablePlayerInternalData(
            directLeaderId = playerData.playerId,
            leaderIdList = playerData.getLeaderAndSelfIdList().toMutableList(),
            aiName = DefaultAI.name(),
        )

        // Split carrier, process pop system data first since it is the most important
        val newPopSystemData: MutablePopSystemData =
            DataSerializer.copy(playerData.playerInternalData.popSystemData())
        newPopSystemData.carrierDataMap.keys.removeAll {
            !carrierIdList.contains(it)
        }
        newPlayerInternalData.popSystemData(newPopSystemData)

        playerData.playerInternalData.popSystemData().carrierDataMap.keys.removeAll {
            carrierIdList.contains(it)
        }

        // copy ai data
        val newAIData: MutableAIData = DataSerializer.copy(playerData.playerInternalData.aiData())
        newPlayerInternalData.aiData(newAIData)

        // copy diplomacy data and remove the war state
        val newDiplomacyData: MutableDiplomacyData =
            DataSerializer.copy(playerData.playerInternalData.diplomacyData())
        newDiplomacyData.relationData.allyMap.clear()
        newDiplomacyData.relationData.enemyIdSet.clear()
        newDiplomacyData.relationData.selfWarDataMap.clear()
        newDiplomacyData.relationData.subordinateWarDataMap.clear()
        newDiplomacyData.relationData.allyWarDataMap.clear()
        newPlayerInternalData.diplomacyData(newDiplomacyData)

        // copy economy data
        val newEconomyData: MutableEconomyData =
            DataSerializer.copy(playerData.playerInternalData.economyData())

        // clear stored resource
        ResourceType.values().forEach { resourceType ->
            ResourceQualityClass.values().forEach { resourceQualityClass ->
                val singleResourceData: MutableSingleResourceData =
                    newEconomyData.resourceData.getSingleResourceData(
                        resourceType,
                        resourceQualityClass
                    )
                singleResourceData.resourceAmount.storage = 0.0
                singleResourceData.resourceAmount.production = 0.0
                singleResourceData.resourceAmount.trade = 0.0
            }
        }

        // Add resource to new player
        ResourceType.values().forEach { resourceType ->
            ResourceQualityClass.values().forEach { resourceQualityClass ->
                newEconomyData.resourceData.addResource(
                    resourceType,
                    playerData.playerInternalData.economyData().resourceData.getResourceQuality(
                        resourceType,
                        resourceQualityClass
                    ),
                    playerData.playerInternalData.economyData().resourceData.getStorageResourceAmount(
                        resourceType,
                        resourceQualityClass
                    ) * storageFraction
                )
            }
        }

        newPlayerInternalData.economyData(newEconomyData)

        // reduce original resource
        playerData.playerInternalData.economyData().resourceData.singleResourceMap.forEach { (_, qualityMap) ->
            qualityMap.forEach { (_, singleResourceData) ->
                singleResourceData.resourceAmount.storage *= (1.0 - storageFraction)
                singleResourceData.resourceAmount.production = 0.0
                singleResourceData.resourceAmount.trade = 0.0
            }
        }

        // Use default modifier data
        val newModifierData = MutableModifierData()
        newPlayerInternalData.modifierData(newModifierData)

        // Use default physics data
        val newPhysicsData = MutablePhysicsData()

        // Check if there is stellar system in player
        val hasStellarSystem: Boolean =
            newPlayerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        // Adjust the fuel proportion based on whether the player has stellar system
        newPhysicsData.fuelRestMassTargetProportionData = if (hasStellarSystem) {
            MutableFuelRestMassTargetProportionData()
        } else {
            MutableFuelRestMassTargetProportionData(
                storage = 0.2,
                movement = 0.5,
                production = 0.2,
                trade = 0.1
            )
        }

        // split fuel to new player
        val fuelToTransfer: Double =
            playerData.playerInternalData.physicsData().fuelRestMassData.storage * storageFraction

        newPhysicsData.addExternalFuel(
            fuelToTransfer
        )
        newPlayerInternalData.physicsData(newPhysicsData)

        // reduce original fuel
        playerData.playerInternalData.physicsData().removeExternalStorageFuel(fuelToTransfer)

        // Copy science data
        val newPlayerScienceData: MutablePlayerScienceData =
            DataSerializer.copy(playerData.playerInternalData.playerScienceData())
        newPlayerInternalData.playerScienceData(newPlayerScienceData)

        // Copy politics data
        val newPoliticsData: MutablePoliticsData =
            DataSerializer.copy(playerData.playerInternalData.politicsData())
        newPlayerInternalData.politicsData(newPoliticsData)


        // Sync data and add the new player internal data to new player list
        newPlayerInternalData.syncDataComponent()
        playerData.newPlayerList.add(newPlayerInternalData)
        playerData.syncData()
    }
}

/**
 * Grant independence to direct subordinate, if the sender is not a top leader, the player
 * still belong to the leader of one level higher
 */
@Serializable
data class GrantIndependenceCommand(
    override val toId: Int,
) : DefaultCommand() {
    override fun name(): String = "Grant Independence"

    override fun description(fromId: Int): I18NString = I18NString(
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
        fromId: Int,
        fromInt4D: Int4D,
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

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val newLeaderIdList: List<Int> = playerData.playerInternalData.leaderIdList.filter {
            (it != playerData.playerInternalData.directLeaderId) && (it != playerData.playerId)
        }
        playerData.changeDirectLeader(newLeaderIdList)
    }
}