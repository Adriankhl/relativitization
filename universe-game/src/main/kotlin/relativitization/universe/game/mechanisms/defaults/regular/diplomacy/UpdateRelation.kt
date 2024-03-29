package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.diplomacy.isAlly
import relativitization.universe.game.data.components.defaults.diplomacy.isEnemy
import relativitization.universe.game.data.components.defaults.modifier.getRelationChange
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.modifierData
import kotlin.random.Random

object UpdateRelation : Mechanism() {
    // Parameters
    // Max relation change by receiving fuel
    private const val inRelationRange: Int = 4

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // Only consider the relation of neighbors, leader, and subordinate
        val playerIdSet: Set<Int> =
            universeData3DAtPlayer.getNeighbourInCube(inRelationRange).map { it.playerId }.toSet() +
                    mutablePlayerData.playerInternalData.leaderIdList.toSet() +
                    mutablePlayerData.playerInternalData.subordinateIdSet.toSet()

        // Filter out dead player
        val playerIdToConsider: Set<Int> = playerIdSet.filter {
            universeData3DAtPlayer.playerDataMap.containsKey(it) && it != mutablePlayerData.playerId
        }.toSet()

        // Relation is stateless, clear it every time
        mutablePlayerData.playerInternalData.diplomacyData().relationData.relationMap.clear()

        playerIdToConsider.forEach {
            val diplomaticRelationReason: DiplomaticRelationReason =
                computeDiplomaticRelationReason(
                    it,
                    mutablePlayerData,
                    universeData3DAtPlayer
                )
            mutablePlayerData.playerInternalData.diplomacyData().relationData.relationMap[it] =
                diplomaticRelationReason.total()
        }

        return listOf()
    }

    fun computeDiplomaticRelationReason(
        otherPlayerId: Int,
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): DiplomaticRelationReason {
        val isLeader: Double = if (mutablePlayerData.isLeader(otherPlayerId)) {
            10.0
        } else {
            0.0
        }

        val isSubordinate: Double = if (mutablePlayerData.isSubOrdinate(otherPlayerId)) {
            5.0
        } else {
            0.0
        }

        val isTopLeaderSame: Double = if (
            universeData3DAtPlayer.get(otherPlayerId)
                .topLeaderId() == mutablePlayerData.topLeaderId()
        ) {
            5.0
        } else {
            0.0
        }

        val isEnemy: Double = if (
            mutablePlayerData.playerInternalData.diplomacyData().relationData.isEnemy(
                otherPlayerId
            )
        ) {
            -20.0
        } else {
            0.0
        }

        val isAlly: Double = if (
            mutablePlayerData.playerInternalData.diplomacyData().relationData.isAlly(
                otherPlayerId
            )
        ) {
            20.0
        } else {
            0.0
        }

        val receivedFuel: Double = mutablePlayerData.playerInternalData.modifierData()
            .diplomacyModifierData.getRelationChange(
                otherPlayerId = otherPlayerId,
                maxReceiveFuelChange = 20.0
            )

        return DiplomaticRelationReason(
            isLeader = isLeader,
            isSubordinate = isSubordinate,
            isTopLeaderSame = isTopLeaderSame,
            isEnemy = isEnemy,
            isAlly = isAlly,
            receivedFuel = receivedFuel,
        )
    }
}

class DiplomaticRelationReason(
    val isLeader: Double,
    val isSubordinate: Double,
    val isTopLeaderSame: Double,
    val isEnemy: Double,
    val isAlly: Double,
    val receivedFuel: Double,
) {
    fun total(): Double = isLeader +
            isSubordinate +
            isTopLeaderSame +
            isEnemy +
            isAlly +
            receivedFuel
}