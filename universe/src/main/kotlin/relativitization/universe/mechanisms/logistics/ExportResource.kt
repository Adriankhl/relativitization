package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendResourceCommand
import relativitization.universe.data.commands.SendResourceToPopCommand
import relativitization.universe.data.components.economy.MutableResourceData
import relativitization.universe.data.components.economy.ResourceQualityData
import relativitization.universe.data.components.popsystem.pop.service.MutableServicePopData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePlayerSingleExportData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePopSingleExportData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.max

object ExportResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        return if (mutablePlayerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0) {
            val exportToPlayerCommandList: List<Command> =
                mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                    val mutableServicePopData: MutableServicePopData = it.allPopData.servicePopData
                    computeExportToPlayerCommands(
                        mutableServicePopData = mutableServicePopData,
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer
                    )
                }.flatten()


            val exportToPopCommandList: List<Command> =
                mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                    val mutableServicePopData: MutableServicePopData = it.allPopData.servicePopData
                    computeExportToPopCommands(
                        mutableServicePopData = mutableServicePopData,
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer
                    )
                }.flatten()

            exportToPlayerCommandList + exportToPopCommandList
        } else {
            listOf()
        }
    }

    /**
     * Compute the fraction of the efficiency of export centers
     */
    fun computePlayerExportFraction(
        mutableServicePopData: MutableServicePopData,
        mutableResourceData: MutableResourceData,
        mutablePlayerSingleExportData: MutablePlayerSingleExportData,
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): Double {
        val targetTopLeaderId: Int = universeData3DAtPlayer.get(
            mutablePlayerSingleExportData.targetPlayerId
        ).topLeaderId()
        val sameTopLeaderId: Boolean = (mutablePlayerData.topLeaderId() == targetTopLeaderId)
        val tariffFactor: Double = if (sameTopLeaderId) {
            0.0
        } else {
            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.getResourceTariffRate(
                topLeaderId = targetTopLeaderId,
                resourceType = mutablePlayerSingleExportData.resourceType
            )
        }

        // Fraction affected by employees
        val totalExportAmount: Double = mutableServicePopData.exportData.totalExportAmount()
        val numEmployee: Double = mutableServicePopData.commonPopData.numEmployee()
        val educationLevelMultiplier: Double =
            (mutableServicePopData.commonPopData.educationLevel * 9.0) + 1.0
        val employeeFraction: Double = if (totalExportAmount > 0.0) {
            numEmployee * educationLevelMultiplier / totalExportAmount
        } else {
            1.0
        }

        // Fraction affected by amount of resource
        val requiredAmount: Double = mutablePlayerSingleExportData.amountPerTime
        val storedAmount: Double = mutableResourceData.getTradeResourceAmount(
            mutablePlayerSingleExportData.resourceType,
            mutablePlayerSingleExportData.resourceQualityClass
        )
        val amountFraction: Double = if (requiredAmount > 0.0) {
            storedAmount / requiredAmount
        } else {
            1.0
        }

        // Fraction affected by the price
        val price: Double = mutableResourceData.getResourcePrice(
            mutablePlayerSingleExportData.resourceType,
            mutablePlayerSingleExportData.resourceQualityClass
        )
        val priceFraction: Double = if ((price * requiredAmount * (1.0 + tariffFactor)) > 0.0) {
            mutablePlayerSingleExportData.storedFuelRestMass / (price * requiredAmount * (1.0 + tariffFactor))
        } else {
            1.0
        }

        return max(
            listOf(
                1.0,
                employeeFraction,
                amountFraction,
                priceFraction
            ).minOf { it },
            0.0
        )
    }

    /**
     * Compute the fraction of the efficiency of export centers
     */
    fun computePopExportFraction(
        mutableServicePopData: MutableServicePopData,
        mutableResourceData: MutableResourceData,
        mutablePopSingleExportData: MutablePopSingleExportData,
        ownerPlayerId: Int,
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Double {
        val targetTopLeaderId: Int = universeData3DAtPlayer.get(
            ownerPlayerId
        ).topLeaderId()
        val sameTopLeaderId: Boolean = (mutablePlayerData.topLeaderId() == targetTopLeaderId)
        val tariffFactor: Double = if (sameTopLeaderId) {
            0.0
        } else {
            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.getResourceTariffRate(
                topLeaderId = targetTopLeaderId,
                resourceType = mutablePopSingleExportData.resourceType
            )
        }

        // Fraction affected by employees
        val totalExportAmount: Double = mutableServicePopData.exportData.totalExportAmount()
        val numEmployee: Double = mutableServicePopData.commonPopData.numEmployee()
        val educationLevelMultiplier: Double =
            (mutableServicePopData.commonPopData.educationLevel * 9.0) + 1.0
        val employeeFraction: Double = if (totalExportAmount > 0.0) {
            numEmployee * educationLevelMultiplier / totalExportAmount
        } else {
            1.0
        }

        // Fraction affected by amount of resource
        val requiredAmount: Double = mutablePopSingleExportData.amountPerTime
        val storedAmount: Double = mutableResourceData.getTradeResourceAmount(
            mutablePopSingleExportData.resourceType,
            mutablePopSingleExportData.resourceQualityClass
        )
        val amountFraction: Double = if (requiredAmount > 0.0) {
            storedAmount / requiredAmount
        } else {
            1.0
        }

        // Fraction affected by the price
        val price: Double = mutableResourceData.getResourcePrice(
            mutablePopSingleExportData.resourceType,
            mutablePopSingleExportData.resourceQualityClass
        )
        val priceFraction: Double = if ((price * requiredAmount * (1.0 + tariffFactor)) > 0.0) {
            mutablePopSingleExportData.storedFuelRestMass / (price * requiredAmount * (1.0 + tariffFactor))
        } else {
            1.0
        }

        return max(
            listOf(
                1.0,
                employeeFraction,
                amountFraction,
                priceFraction
            ).minOf { it },
            0.0
        )
    }

    /**
     * Compute export to player command
     */
    fun computeExportToPlayerCommands(
        mutableServicePopData: MutableServicePopData,
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): List<Command> {

        return mutableServicePopData.exportData.playerExportCenterMap.map { (_, exportCenterData) ->
            exportCenterData.exportDataList.map { mutablePlayerSingleExportData ->
                val targetTopLeaderId: Int = universeData3DAtPlayer.get(
                    mutablePlayerSingleExportData.targetPlayerId
                ).topLeaderId()
                val sameTopLeaderId: Boolean =
                    (mutablePlayerData.topLeaderId() == targetTopLeaderId)
                val tariffFactor: Double = if (sameTopLeaderId) {
                    0.0
                } else {
                    mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.getResourceTariffRate(
                        topLeaderId = targetTopLeaderId,
                        resourceType = mutablePlayerSingleExportData.resourceType
                    )
                }

                // Compute the quality and amount
                val resourceData: MutableResourceData =
                    mutablePlayerData.playerInternalData.economyData().resourceData

                val exportFraction: Double = computePlayerExportFraction(
                    mutableServicePopData = mutableServicePopData,
                    mutableResourceData = resourceData,
                    mutablePlayerSingleExportData = mutablePlayerSingleExportData,
                    mutablePlayerData = mutablePlayerData,
                    universeData3DAtPlayer = universeData3DAtPlayer,
                )

                val amount: Double = mutablePlayerSingleExportData.amountPerTime * exportFraction

                val resourceQualityData: ResourceQualityData = resourceData.getResourceQuality(
                    resourceType = mutablePlayerSingleExportData.resourceType,
                    resourceQualityClass = mutablePlayerSingleExportData.resourceQualityClass
                ).toResourceQualityData()

                val price: Double = resourceData.getResourcePrice(
                    resourceType = mutablePlayerSingleExportData.resourceType,
                    resourceQualityClass = mutablePlayerSingleExportData.resourceQualityClass
                )

                // Consume resource
                resourceData.getResourceAmountData(
                    resourceType = mutablePlayerSingleExportData.resourceType,
                    resourceQualityClass = mutablePlayerSingleExportData.resourceQualityClass
                ).trade -= amount
                mutablePlayerSingleExportData.storedFuelRestMass -= price * amount * (1.0 + tariffFactor)

                // Add tariff to player storage
                mutablePlayerData.playerInternalData.physicsData().addFuel(price * amount)
                mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass += price * amount * tariffFactor

                SendResourceCommand(
                    toId = mutablePlayerSingleExportData.targetPlayerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    resourceType = mutablePlayerSingleExportData.resourceType,
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
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ): List<Command> {

        return mutableServicePopData.exportData.popExportCenterMap.map { (ownerPlayerId, exportCenterData) ->
            exportCenterData.exportDataMap.map { (carrierId, popTypeMap) ->
                popTypeMap.map { (popType, exportDataList) ->
                    exportDataList.map { mutablePopSingleExportData ->

                        val targetTopLeaderId: Int = universeData3DAtPlayer.get(
                            ownerPlayerId
                        ).topLeaderId()
                        val sameTopLeaderId: Boolean =
                            (mutablePlayerData.topLeaderId() == targetTopLeaderId)
                        val tariffFactor: Double = if (sameTopLeaderId) {
                            0.0
                        } else {
                            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff.getResourceTariffRate(
                                topLeaderId = targetTopLeaderId,
                                resourceType = mutablePopSingleExportData.resourceType
                            )
                        }


                        // Compute the quality and amount
                        val resourceData: MutableResourceData =
                            mutablePlayerData.playerInternalData.economyData().resourceData

                        val exportFraction: Double = computePopExportFraction(
                            mutableServicePopData = mutableServicePopData,
                            mutableResourceData = resourceData,
                            mutablePopSingleExportData = mutablePopSingleExportData,
                            ownerPlayerId = ownerPlayerId,
                            mutablePlayerData = mutablePlayerData,
                            universeData3DAtPlayer = universeData3DAtPlayer,
                        )

                        val amount: Double =
                            mutablePopSingleExportData.amountPerTime * exportFraction

                        val resourceQualityData: ResourceQualityData =
                            resourceData.getResourceQuality(
                                resourceType = mutablePopSingleExportData.resourceType,
                                resourceQualityClass = mutablePopSingleExportData.resourceQualityClass
                            ).toResourceQualityData()

                        val price: Double = resourceData.getResourcePrice(
                            resourceType = mutablePopSingleExportData.resourceType,
                            resourceQualityClass = mutablePopSingleExportData.resourceQualityClass
                        )

                        // Consume resource
                        resourceData.getResourceAmountData(
                            resourceType = mutablePopSingleExportData.resourceType,
                            resourceQualityClass = mutablePopSingleExportData.resourceQualityClass
                        ).trade -= amount
                        mutablePopSingleExportData.storedFuelRestMass -= price * amount * (1.0 + tariffFactor)

                        // Add tariff to player storage
                        mutablePlayerData.playerInternalData.physicsData().addFuel(price * amount)
                        mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass += price * amount * tariffFactor

                        SendResourceToPopCommand(
                            toId = ownerPlayerId,
                            fromId = mutablePlayerData.playerId,
                            fromInt4D = mutablePlayerData.int4D.toInt4D(),
                            targetCarrierId = carrierId,
                            targetPopType = popType,
                            resourceType = mutablePopSingleExportData.resourceType,
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