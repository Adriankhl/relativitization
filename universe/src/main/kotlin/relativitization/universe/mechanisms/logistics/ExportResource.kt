package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendResourceCommand
import relativitization.universe.data.component.economy.MutableResourceData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.component.popsystem.pop.MutableAllPopData
import relativitization.universe.data.component.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object ExportResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach {
            val mutableServicePopData: MutableServicePopData = it.allPopData.servicePopData
            val exportFraction: Double = computeExportFraction(mutableServicePopData)
        }

        return listOf()
    }

    /**
     * Compute the fraction of the efficiency of export centers
     */
    fun computeExportFraction(
        mutableServicePopData: MutableServicePopData
    ) : Double {
        val numEmployee: Double = mutableServicePopData.commonPopData.numEmployee()

        val educationLevelMultiplier: Double = (mutableServicePopData.commonPopData.educationLevel * 9.0) + 1.0

        val totalExportAmount: Double = mutableServicePopData.exportData.totalExportAmount()

        val fraction: Double = if (totalExportAmount > 0.0) {
            numEmployee * educationLevelMultiplier / totalExportAmount
        } else {
            1.0
        }

        return when {
            fraction > 1.0 -> 1.0
            fraction < 0.0 -> 0.0
            else -> fraction
        }
    }

    /**
     * Compute export to player command
     */
    fun computeExportToPlayerCommands(
        mutableServicePopData: MutableServicePopData,
        mutablePlayerData: MutablePlayerData,
        exportFraction: Double,
    ): List<Command> {

        return mutableServicePopData.exportData.playerExportCenterMap.map { (ownerPlayerId, exportData) ->
            exportData.exportDataList.map {

                // Compute the quality and amount
                val resourceData: MutableResourceData = mutablePlayerData.playerInternalData.economyData().resourceData
                val totalAmount: Double = resourceData.getTradeResourceAmount(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                )
                val amount: Double = if (totalAmount > (it.amountPerTime * exportFraction)) {
                    it.amountPerTime * exportFraction
                } else {
                    totalAmount
                }

                val resourceQualityData: ResourceQualityData = resourceData.getResourceQuality(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                ).toResourceQualityData()

                // Consume resource
                resourceData.getResourceAmountData(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                ).trade -= amount

                SendResourceCommand(
                    toId = it.targetPlayerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    resourceType = it.resourceType,
                    resourceQualityData = resourceQualityData,
                    amount = amount,
                    senderResourceLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance,
                )
            }
        }.flatten()
    }
}