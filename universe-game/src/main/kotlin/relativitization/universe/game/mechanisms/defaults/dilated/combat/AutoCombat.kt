package relativitization.universe.game.mechanisms.defaults.dilated.combat

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.commands.DamageCommand
import relativitization.universe.game.data.components.MutablePhysicsData
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import kotlin.math.min
import kotlin.random.Random

object AutoCombat : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // enemy in the same cube
        val sameCubeEnemy: List<PlayerData> = computeEnemyList(
            mutablePlayerData,
            universeData3DAtPlayer.getNeighbourInCube(1)
        ).shuffled(random)

        val physicsData: MutablePhysicsData = mutablePlayerData.playerInternalData.physicsData()

        return if (sameCubeEnemy.isNotEmpty()) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                // Randomly pick target enemy
                val targetEnemy: PlayerData = sameCubeEnemy[random.nextInt(sameCubeEnemy.size)]

                val attack: Double = min(
                    it.allPopData.soldierPopData.militaryBaseData.attack,
                    physicsData.fuelRestMassData.production
                )

                // Consume production fuel when attack
                physicsData.removeExternalProductionFuel(attack)

                // Adjust damage by time dilation
                val command = DamageCommand(
                    toId = targetEnemy.playerId,
                    attack = attack
                )
                command
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
        neighborList: List<PlayerData>,
    ): List<PlayerData> {
        // player view the neighbor as enemy, or neighbor that views the player as enemy
        return neighborList.filter { neighbor ->
            mutablePlayerData.playerInternalData.diplomacyData().relationData.enemyIdSet
                .contains(neighbor.playerId) ||
                    neighbor.playerInternalData.diplomacyData().relationData.enemyIdSet
                        .contains(mutablePlayerData.playerId)
        }
    }
}