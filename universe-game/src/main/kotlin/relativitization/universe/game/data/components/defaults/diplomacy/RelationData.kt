package relativitization.universe.game.data.components.defaults.diplomacy

import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.diplomacy.ally.MutableAllianceData
import relativitization.universe.game.data.components.defaults.diplomacy.war.MutableWarData

/**
 * Store diplomatic relation
 *
 * @property relationMap map from other player id to relation
 * @property enemyIdSet the set of enemy, determined by wars of self, leader, subordinate, ally
 * @property allyMap map from ally id to alliance data
 * @property selfWarDataMap map from enemy id to the war between the enemy and self
 * @property subordinateWarDataMap map from subordinate id to opponent id to the war of
 *  subordinate which this player has joined
 * @property allyWarDataMap map from ally id to opponent id to the war of ally which this
 *  player has joined
 */
@GenerateImmutable
data class MutableRelationData(
    val relationMap: MutableMap<Int, Double> = mutableMapOf(),
    val enemyIdSet: MutableSet<Int> = mutableSetOf(),
    val allyMap: MutableMap<Int, MutableAllianceData> = mutableMapOf(),
    val selfWarDataMap: MutableMap<Int, MutableWarData> = mutableMapOf(),
    val subordinateWarDataMap: MutableMap<Int, MutableMap<Int, MutableWarData>> = mutableMapOf(),
    val allyWarDataMap: MutableMap<Int, MutableMap<Int, MutableWarData>> = mutableMapOf(),
    val allySubordinateWarDataMap: MutableMap<Int, MutableMap<Int, MutableMap<Int, MutableWarData>>> = mutableMapOf(),
) {
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

    fun addAllySubordinateWar(allyId: Int, warData: MutableWarData) {
        allySubordinateWarDataMap.getOrPut(allyId) {
            mutableMapOf()
        }.getOrPut(warData.warCoreData.supportId) {
            mutableMapOf()
        }[warData.warCoreData.opponentId] = warData
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

fun RelationData.isEnemy(playerId: Int): Boolean {
    return enemyIdSet.contains(playerId)
}

fun MutableRelationData.isEnemy(playerId: Int): Boolean {
    return enemyIdSet.contains(playerId)
}

fun RelationData.isAlly(playerId: Int): Boolean {
    return if (isEnemy(playerId)) {
        false
    } else {
        allyMap.containsKey(playerId)
    }
}

fun MutableRelationData.isAlly(playerId: Int): Boolean {
    return if (isEnemy(playerId)) {
        false
    } else {
        allyMap.keys.contains(playerId)
    }
}

fun RelationData.hasSubordinateWar(
    subordinateId: Int,
    opponentId: Int
): Boolean {
    return if (subordinateWarDataMap.containsKey(subordinateId)) {
        subordinateWarDataMap.getValue(subordinateId).containsKey(opponentId)
    } else {
        false
    }
}

fun MutableRelationData.hasSubordinateWar(
    subordinateId: Int,
    opponentId: Int
): Boolean {
    return if (subordinateWarDataMap.containsKey(subordinateId)) {
        subordinateWarDataMap.getValue(subordinateId).containsKey(opponentId)
    } else {
        false
    }
}

fun RelationData.hasAllyWar(
    allyId: Int,
    opponentId: Int
): Boolean {
    return if (allyWarDataMap.containsKey(allyId)) {
        allyWarDataMap.getValue(allyId).containsKey(opponentId)
    } else {
        false
    }
}

fun MutableRelationData.hasAllyWar(
    allyId: Int,
    opponentId: Int
): Boolean {
    return if (allyWarDataMap.containsKey(allyId)) {
        allyWarDataMap.getValue(allyId).containsKey(opponentId)
    } else {
        false
    }
}


fun RelationData.hasAllySubordinateWar(
    allyId: Int,
    allySubordinateId: Int,
    opponentId: Int,
): Boolean {
    return if (allySubordinateWarDataMap.containsKey(allyId)) {
        if (allySubordinateWarDataMap.getValue(allyId).containsKey(allySubordinateId)) {
            allySubordinateWarDataMap.getValue(allyId).getValue(allySubordinateId)
                .containsKey(opponentId)
        } else {
            false
        }
    } else {
        false
    }
}

fun MutableRelationData.hasAllySubordinateWar(
    allyId: Int,
    allySubordinateId: Int,
    opponentId: Int,
): Boolean {
    return if (allySubordinateWarDataMap.containsKey(allyId)) {
        if (allySubordinateWarDataMap.getValue(allyId).containsKey(allySubordinateId)) {
            allySubordinateWarDataMap.getValue(allyId).getValue(allySubordinateId)
                .containsKey(opponentId)
        } else {
            false
        }
    } else {
        false
    }
}

fun RelationData.getRelation(playerId: Int): Double = relationMap.getOrDefault(
    playerId,
    0.0
)

fun MutableRelationData.getRelation(playerId: Int): Double = relationMap.getOrDefault(
    playerId,
    0.0
)

fun RelationData.allWarTargetId(): Set<Int> = selfWarDataMap.keys +
        subordinateWarDataMap.values.flatMap {
            it.keys
        } +
        allyWarDataMap.values.flatMap {
            it.keys
        } +
        allySubordinateWarDataMap.values.flatMap { outerMap ->
            outerMap.values.flatMap { innerMap ->
                innerMap.keys
            }
        }

fun MutableRelationData.allWarTargetId(): Set<Int> = selfWarDataMap.keys +
        subordinateWarDataMap.values.flatMap {
            it.keys
        } +
        allyWarDataMap.values.flatMap {
            it.keys
        } +
        allySubordinateWarDataMap.values.flatMap { outerMap ->
            outerMap.values.flatMap { innerMap ->
                innerMap.keys
            }
        }


fun RelationData.allOffensiveWarTargetId(): Set<Int> {
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

    val allySubordinateWarId: Set<Int> = allySubordinateWarDataMap.values.flatMap { outerMap ->
        outerMap.values.flatMap { innerMap ->
            innerMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }
    }.toSet()

    return selfWarId + subordinateWarId + allyWarId + allySubordinateWarId
}

fun MutableRelationData.allOffensiveWarTargetId(): Set<Int> {
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

    val allySubordinateWarId: Set<Int> = allySubordinateWarDataMap.values.flatMap { outerMap ->
        outerMap.values.flatMap { innerMap ->
            innerMap.filterValues {
                it.warCoreData.isOffensive
            }.keys
        }
    }.toSet()

    return selfWarId + subordinateWarId + allyWarId + allySubordinateWarId
}