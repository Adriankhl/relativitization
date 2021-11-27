package relativitization.universe.mechanisms.defaults.combat

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DamageCommand
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object AutoCombat : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )

        // enemy in the same cube
        val sameCubeEnemy: List<PlayerData> = computeEnemyList(
            mutablePlayerData,
            universeData3DAtPlayer,
            universeData3DAtPlayer.getNeighbour(0)
        ).shuffled()

        return if (sameCubeEnemy.isNotEmpty()) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                // Randomly pick target enemy
                val targetEnemy: PlayerData = sameCubeEnemy[Random.nextInt(sameCubeEnemy.size)]

                // Adjust damage by time dilation
                DamageCommand(
                    toId = targetEnemy.playerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    attack = it.allPopData.soldierPopData.militaryBaseData.attack / gamma
                )
            }
        } else {
            listOf()
        }
    }

    /**
     * Compute list of nearby enemy
     */
    fun computeEnemyList(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        neighbors: List<PlayerData>,
    ): List<PlayerData> {

        // Neighbor that views the player as enemy
        val neighborViewEnemyList: List<PlayerData> =
            neighbors.filter { playerData ->
                playerData.playerInternalData.diplomacyData().isEnemyOf(
                    mutablePlayerData
                )
            }


        // Neighbor that this player views as enemy
        val selfViewEnemyList: List<PlayerData> =
            mutablePlayerData.playerInternalData.diplomacyData().relationMap.filter { (id, relationData) ->
                // Select the player that are enemy, nearby, and not in enemyOfSelf list
                (relationData.diplomaticRelationState == DiplomaticRelationState.ENEMY) && neighbors.any { playerData ->
                    playerData.playerId == id
                } && neighborViewEnemyList.all { playerData ->
                    playerData.playerId != id
                }
            }.map { (playerId, _) ->
                universeData3DAtPlayer.get(playerId)
            }

        return selfViewEnemyList + neighborViewEnemyList
    }
}