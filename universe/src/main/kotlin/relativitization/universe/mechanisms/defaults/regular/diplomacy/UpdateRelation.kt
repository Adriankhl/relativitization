package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.modifierData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateRelation : Mechanism() {
    // Parameters
    // Max relation change by receiving fuel
    private const val inRelationRange: Int = 4

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
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