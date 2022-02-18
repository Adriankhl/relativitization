package relativitization.universe.mechanisms.defaults.dilated.combat

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.DamageCommand
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.maths.random.Rand
import kotlin.math.min

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
            universeData3DAtPlayer.getNeighbour(0)
        ).shuffled(Rand.rand())

        val fuelRestMassData: MutableFuelRestMassData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassData

        return if (sameCubeEnemy.isNotEmpty()) {
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                // Randomly pick target enemy
                val targetEnemy: PlayerData = sameCubeEnemy[Rand.rand().nextInt(sameCubeEnemy.size)]

                val attack: Double = min(
                    it.allPopData.soldierPopData.militaryBaseData.attack,
                    fuelRestMassData.production
                )

                // Consume production fuel when attack
                fuelRestMassData.production -= attack

                // Adjust damage by time dilation
                val command = DamageCommand(
                    toId = targetEnemy.playerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
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
            mutablePlayerData.playerInternalData.diplomacyData().isEnemyOf(neighbor) ||
                    neighbor.playerInternalData.diplomacyData().isEnemyOf(mutablePlayerData)
        }
    }
}