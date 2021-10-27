package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendResourceCommand
import relativitization.universe.data.commands.SendResourceToPopCommand
import relativitization.universe.data.component.economy.MutableResourceData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.component.popsystem.pop.service.export.MutablePlayerSingleExportData
import relativitization.universe.data.component.popsystem.pop.service.export.MutablePopSingleExportData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object ExportResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val exportToPlayerCommandList: List<Command> =
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                val mutableServicePopData: MutableServicePopData = it.allPopData.servicePopData
                computeExportToPlayerCommands(
                    mutableServicePopData = mutableServicePopData,
                    mutablePlayerData = mutablePlayerData,
                )
            }.flatten()

        return exportToPlayerCommandList
    }

    /**
     * Compute the fraction of the efficiency of export centers
     */
    fun computePlayerExportFraction(
        mutableServicePopData: MutableServicePopData,
        mutableResourceData: MutableResourceData,
        mutablePlayerSingleExportData: MutablePlayerSingleExportData,
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
     * Compute the fraction of the efficiency of export centers
     */
    fun computePopExportFraction(
        mutableServicePopData: MutableServicePopData,
        mutableResourceData: MutableResourceData,
        mutablePopSingleExportData: MutablePopSingleExportData
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
    ): List<Command> {

        return mutableServicePopData.exportData.playerExportCenterMap.map { (_, exportCenterData) ->
            exportCenterData.exportDataList.map {

                // Compute the quality and amount
                val resourceData: MutableResourceData = mutablePlayerData.playerInternalData.economyData().resourceData

                val exportFraction: Double = computePlayerExportFraction(
                    mutableServicePopData = mutableServicePopData,
                    mutableResourceData = resourceData,
                    mutablePlayerSingleExportData = it
                )

                val amount: Double = it.amountPerTime * exportFraction

                val resourceQualityData: ResourceQualityData = resourceData.getResourceQuality(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                ).toResourceQualityData()

                val price: Double = resourceData.getResourcePrice(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                )

                // Consume resource
                resourceData.getResourceAmountData(
                    resourceType = it.resourceType,
                    resourceQualityClass = it.resourceQualityClass
                ).trade -= amount
                it.storedFuelRestMass -= price * amount

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

    /**
     * Compute export to pop command
     */
    fun computeExportToPopCommands(
        mutableServicePopData: MutableServicePopData,
        mutablePlayerData: MutablePlayerData,
        exportFraction: Double,
    ): List<Command> {

        return mutableServicePopData.exportData.popExportCenterMap.map { (ownerPlayerId, exportCenterData) ->
            exportCenterData.exportDataMap.map { (carrierId, popTypeMap) ->
                popTypeMap.map { (popType, exportDataList) ->
                    exportDataList.map {

                        // Compute the quality and amount
                        val resourceData: MutableResourceData =
                            mutablePlayerData.playerInternalData.economyData().resourceData

                        val exportFraction: Double = computePopExportFraction(
                            mutableServicePopData = mutableServicePopData,
                            mutableResourceData = resourceData,
                            mutablePopSingleExportData = it
                        )

                        val amount: Double = it.amountPerTime * exportFraction

                        val resourceQualityData: ResourceQualityData = resourceData.getResourceQuality(
                            resourceType = it.resourceType,
                            resourceQualityClass = it.resourceQualityClass
                        ).toResourceQualityData()

                        val price: Double = resourceData.getResourcePrice(
                            resourceType = it.resourceType,
                            resourceQualityClass = it.resourceQualityClass
                        )

                        // Consume resource
                        resourceData.getResourceAmountData(
                            resourceType = it.resourceType,
                            resourceQualityClass = it.resourceQualityClass
                        ).trade -= amount
                        it.storedFuelRestMass -= price * amount

                        SendResourceToPopCommand(
                            toId = ownerPlayerId,
                            fromId = mutablePlayerData.playerId,
                            fromInt4D = mutablePlayerData.int4D.toInt4D(),
                            targetCarrierId = carrierId,
                            targetPopType = popType,
                            resourceType = it.resourceType,
                            resourceQualityData = resourceQualityData,
                            amount = amount,
                            senderResourceLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance,
                        )
                    }
                }
            }
        }.flatten().flatten().flatten()
    }
}