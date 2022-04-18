package relativitization.universe.data.components.defaults.diplomacy

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.diplomacy.ally.AllianceData
import relativitization.universe.data.components.defaults.diplomacy.ally.MutableAllianceData
import relativitization.universe.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.data.components.defaults.diplomacy.war.WarData
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Store diplomatic relation
 *
 * @property relationMap map from other player id to relation
 * @property enemyIdSet the set of enemy, determined by wars of self, leader, subordinate, ally
 * @property allyMap map from ally id to alliance data
 * @property selfWarDataMap map from enemy id to the war between the enemy and self
 * @property subordinateWarDataMap map from subordinate id to opponent id to the war of
 * subordinate which this player has joined
 * @property allyWarDataMap map from ally id to opponent id to the war of ally which this
 * player has joined
 */
@Serializable
data class RelationData(
    val relationMap: Map<Int, Double> = mapOf(),
    val enemyIdSet: Set<Int> = setOf(),
    val allyMap: Map<Int, AllianceData> = mapOf(),
    val selfWarDataMap: Map<Int, WarData> = mapOf(),
    val subordinateWarDataMap: Map<Int, Map<Int, WarData>> = mapOf(),
    val allyWarDataMap: Map<Int, Map<Int, WarData>> = mapOf(),
) {
    fun isEnemy(playerId: Int): Boolean {
        return enemyIdSet.contains(playerId)
    }

    fun isAlly(playerId: Int): Boolean {
        return if (isEnemy(playerId)) {
            false
        } else {
            allyMap.keys.contains(playerId)
        }
    }

    fun getRelation(playerId: Int): Double = relationMap.getOrDefault(playerId, 0.0)

    fun allWarTargetId(): Set<Int> = selfWarDataMap.keys + subordinateWarDataMap.values.flatMap {
        it.keys
    } + allyWarDataMap.values.flatMap { it.keys }

    fun allOffensiveWarTargetId(): Set<Int> {
        val selfWarId: Set<Int> = selfWarDataMap.filterValues {
            it.warCoreData.isOffensive
        }.keys

        val subordinateWarId: Set<Int> = subordinateWarDataMap.values.flatMap { warDataMap ->
            warDataMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }.toSet()

        val allyWarId: Set<Int> = allyWarDataMap.values.flatMap { warDataMap ->
            warDataMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }.toSet()

        return selfWarId + subordinateWarId + allyWarId
    }
}

@Serializable
data class MutableRelationData(
    val relationMap: MutableMap<Int, Double> = mutableMapOf(),
    val enemyIdSet: MutableSet<Int> = mutableSetOf(),
    val allyMap: MutableMap<Int, MutableAllianceData> = mutableMapOf(),
    val selfWarDataMap: MutableMap<Int, MutableWarData> = mutableMapOf(),
    val subordinateWarDataMap: MutableMap<Int, MutableMap<Int, MutableWarData>> = mutableMapOf(),
    val allyWarDataMap: MutableMap<Int, MutableMap<Int, MutableWarData>> = mutableMapOf(),
) {
    fun isEnemy(playerId: Int): Boolean {
        return enemyIdSet.contains(playerId)
    }

    fun isAlly(playerId: Int): Boolean {
        return if (isEnemy(playerId)) {
            false
        } else {
            allyMap.keys.contains(playerId)
        }
    }

    fun hasSubordinateWar(
        subordinateId: Int,
        opponentId: Int
    ): Boolean {
        return if (subordinateWarDataMap.containsKey(subordinateId)) {
            subordinateWarDataMap.getValue(subordinateId).containsKey(opponentId)
        } else {
            false
        }
    }

    fun hasAllyWar(
        allyId: Int,
        opponentId: Int
    ): Boolean {
        return if (allyWarDataMap.containsKey(allyId)) {
            allyWarDataMap.getValue(allyId).containsKey(opponentId)
        } else {
            false
        }
    }

    fun getRelation(playerId: Int): Double = relationMap.getOrDefault(playerId, 0.0)

    fun allWarTargetId(): Set<Int> = selfWarDataMap.keys + subordinateWarDataMap.values.flatMap {
        it.keys
    } + allyWarDataMap.values.flatMap { it.keys }

    fun allOffensiveWarTargetId(): Set<Int> {
        val selfWarId: Set<Int> = selfWarDataMap.filterValues {
            it.warCoreData.isOffensive
        }.keys

        val subordinateWarId: Set<Int> = subordinateWarDataMap.values.flatMap { warDataMap ->
            warDataMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }.toSet()

        val allyWarId: Set<Int> = allyWarDataMap.values.flatMap { warDataMap ->
            warDataMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }.toSet()

        return selfWarId + subordinateWarId + allyWarId
    }

    fun addSelfWar(warData: MutableWarData) {
        if (!selfWarDataMap.containsKey(warData.warCoreData.opponentId)) {
            selfWarDataMap[warData.warCoreData.opponentId] = warData
        } else {
            // Consider changing the defensive war status if there is a duplicate war declaration
            val currentWarData: MutableWarData = selfWarDataMap.getValue(
                warData.warCoreData.opponentId
            )
            if (!currentWarData.warCoreData.isDefensive) {
                currentWarData.warCoreData = currentWarData.warCoreData.copy(
                    isDefensive = warData.warCoreData.isDefensive
                )
            }
        }
    }

    fun addSubordinateWar(warData: MutableWarData) {
        subordinateWarDataMap.getOrPut(warData.warCoreData.supportId) {
            mutableMapOf()
        }[warData.warCoreData.opponentId] = warData
    }

    fun addAllyWar(warData: MutableWarData) {
        allyWarDataMap.getOrPut(warData.warCoreData.supportId) {
            mutableMapOf()
        }[warData.warCoreData.opponentId] = warData
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}