package relativitization.universe.mechanisms.primary.combat

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DamageCommand
import relativitization.universe.data.components.diplomacy.DiplomaticRelationState
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object AutoCombat : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

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
                DamageCommand(
                    toId = targetEnemy.playerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    attack = it.allPopData.soldierPopData.militaryBaseData.attack
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

        val enemyOfSelf: List<PlayerData> =
            neighbors.filter { playerData ->
                    playerData.playerInternalData.diplomacyData().isEnemyOf(
                        mutablePlayerData
                    )
                }

        val selfEnemy: List<PlayerData> =
            mutablePlayerData.playerInternalData.diplomacyData().relationMap.filter { (id, relationData) ->
                // Select the player that are enemy, nearby, and not in enemyOfSelf list
                (relationData.diplomaticRelationState == DiplomaticRelationState.ENEMY) && neighbors.any { playerData ->
                    playerData.playerId == id
                } && enemyOfSelf.all { playerData ->
                    playerData.playerId != id
                }
            }.map { (playerId, _) ->
                universeData3DAtPlayer.get(playerId)
            }

        return selfEnemy + enemyOfSelf
    }
}