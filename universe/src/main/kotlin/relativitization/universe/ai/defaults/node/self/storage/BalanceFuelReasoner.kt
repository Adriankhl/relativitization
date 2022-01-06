package relativitization.universe.ai.defaults.node.self.storage

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.ai.defaults.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeStorageFuelTargetCommand
import relativitization.universe.data.commands.TransferFuelToMovementCommand
import relativitization.universe.data.commands.TransferFuelToProductionCommand
import relativitization.universe.data.commands.TransferFuelToTradeCommand
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.CarrierType

class BalanceFuelReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        BalanceFuelDataAINode(),
        BalanceFuelTargetDataAINode(),
    )
}

/**
 * Transfer from storage to other fuel usage categories
 */
class BalanceFuelDataAINode : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val storage: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.storage
        val movement: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.movement
        val production: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.production
        val trade: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.trade
        val totalFuel: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .physicsData().fuelRestMassData.total()

        // Try to distribute resource evenly
        // If there is stellar system in player, don't transfer to movement
        val hasStellarSystem: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.values.any {
                it.carrierType == CarrierType.STELLAR
            }

        // To compute the fraction target
        val storageWeight: Double = 1.0
        val movementWeight: Double = if (hasStellarSystem) {
            0.0
        } else {
            1.0
        }
        val productionWeight: Double = 2.0
        val tradeWeight: Double = 1.0
        val totalWeight: Double = storageWeight + movementWeight + productionWeight + tradeWeight

        val storageFraction: Double = storageWeight / totalWeight
        val movementFraction: Double = movementWeight / totalWeight
        val productionFraction: Double = productionWeight / totalWeight
        val tradeFraction: Double = tradeWeight / totalWeight

        // Only balance the fuel if storage is sufficient
        val isStorageEnough: Boolean = if (totalFuel > 0.0) {
            (storage / totalFuel) >= storageFraction
        } else {
            false
        }

        if (isStorageEnough) {
            val availableStorage: Double = storage - totalFuel * storageFraction

            // Calculate which category is lacking
            val movementLack: Double = if ((movement / totalFuel) < movementFraction) {
                movementFraction * totalFuel - movement
            } else {
                0.0
            }
            val productionLack: Double = if ((production / totalFuel) < productionFraction) {
                productionFraction * totalFuel - production
            } else {
                0.0
            }
            val tradeLack: Double = if ((trade / totalFuel) < tradeFraction) {
                tradeFraction * totalFuel - trade
            } else {
                0.0
            }
            val totalLack: Double = movementLack + productionLack + tradeLack

            // Transfer to movement fuel is too low
            if (movementLack > 0.0) {
                planDataAtPlayer.addCommand(
                    TransferFuelToMovementCommand(
                        toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                        amount = availableStorage * movementLack / totalLack,
                    )
                )
            }

            // Transfer to production fuel is too low
            if (productionLack > 0.0) {
                planDataAtPlayer.addCommand(
                    TransferFuelToProductionCommand(
                        toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                        amount = availableStorage * productionLack / totalLack,
                    )
                )
            }

            // Transfer to trade fuel is too low
            if (tradeLack > 0.0) {
                planDataAtPlayer.addCommand(
                    TransferFuelToTradeCommand(
                        toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                        amount = availableStorage * tradeLack / totalLack,
                    )
                )
            }
        }
    }
}

/**
 * Change the fuel target data
 */
class BalanceFuelTargetDataAINode : AINode {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Set the target storage to a high value, which means everything is put into the storage
        // Do the transfer "manually" by AI
        val currentTargetStorage: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().targetFuelRestMassData.storage

        if (currentTargetStorage < 1E100) {
            planDataAtPlayer.addCommand(
                ChangeStorageFuelTargetCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    targetAmount = 1E100,
                )
            )
        }
    }
}