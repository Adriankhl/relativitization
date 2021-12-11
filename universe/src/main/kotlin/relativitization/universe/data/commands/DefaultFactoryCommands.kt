package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.*
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Build a fuel factory on player
 *
 * @property senderTopLeaderId the player id of the top leader of the sender
 * @property targetCarrierId build factory on that carrier
 * @property ownerId who own this factory
 * @property fuelFactoryInternalData data of the factory
 * @property storedFuelRestMass fuel stored in the newly built factory
 * @property numBuilding number of building in this factory
 */
@Serializable
data class BuildForeignFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val senderTopLeaderId: Int,
    val targetCarrierId: Int,
    val ownerId: Int,
    val fuelFactoryInternalData: FuelFactoryInternalData,
    val storedFuelRestMass: Double,
    val numBuilding: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Build a foreign fuel factory owned by player "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". Initial stored fuel rest mass: "),
            IntString(3),
            NormalString(", number of building: "),
            IntString(4),
            NormalString(", max. output: "),
            IntString(5),
            NormalString(", max. employee: "),
            IntString(6),
            NormalString(". "),
        ),
        listOf(
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            storedFuelRestMass.toString(),
            numBuilding.toString(),
            (numBuilding * fuelFactoryInternalData.maxOutputAmount).toString(),
            (numBuilding * fuelFactoryInternalData.maxNumEmployee).toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val sameTopLeaderIdI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    NormalString("Top leader id "),
                    IntString(0),
                    NormalString(" is not equal to "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    senderTopLeaderId.toString(),
                    playerData.topLeaderId().toString(),
                ),
            )
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowConstruction: Boolean =
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory
        val allowConstructionI18NString: I18NString = if (allowConstruction) {
            I18NString("")
        } else {
            I18NString("Not allow to build factory, not a top leader")
        }

        val validFactoryInternalData: Boolean = fuelFactoryInternalData.squareDiff(
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryInternalData()
        ) < 0.1
        val validFactoryInternalDataI18NString: I18NString = if (validFactoryInternalData) {
            I18NString("")
        } else {
            I18NString("Factory internal data is not valid. ")
        }

        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass
        val hasFuelI18NString: I18NString = if (hasFuel) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }


        return CommandMessage(
            sameTopLeaderId && allowConstruction && validFactoryInternalData && hasFuel,
            listOf(
                sameTopLeaderIdI18NString,
                allowConstructionI18NString,
                validFactoryInternalDataI18NString,
                hasFuelI18NString
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= fuelNeeded + storedFuelRestMass

    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId

        val canForeignInvestorBuild: Boolean = (!sameTopLeaderId &&
                playerData.playerInternalData.politicsData().allowForeignInvestor)

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()

        val canSubordinateBuild: Boolean = (!isSenderTopLeader &&
                playerData.playerInternalData.politicsData().allowSubordinateBuildFactory)

        val allowConstruction: Boolean =
            isSenderTopLeader || canForeignInvestorBuild || canSubordinateBuild

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        return allowConstruction && hasCarrier
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(targetCarrierId)
        carrier.allPopData.labourerPopData.addFuelFactory(
            MutableFuelFactoryData(
                ownerPlayerId = ownerId,
                fuelFactoryInternalData = DataSerializer.copy(fuelFactoryInternalData),
                numBuilding = numBuilding,
                isOpened = true,
                storedFuelRestMass = storedFuelRestMass,
                lastOutputAmount = 0.0,
                lastNumEmployee = 0.0
            )
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Build a resource factory on player
 *
 * @property senderTopLeaderId the player id of the top leader of the sender
 * @property targetCarrierId build factory on that carrier
 * @property ownerId who own this factory
 * @property resourceFactoryInternalData data of the factory
 * @property qualityLevel the quality of the factory, relative to tech level
 * @property storedFuelRestMass fuel stored in the newly built factory
 * @property numBuilding number of building in this factory
 */
@Serializable
data class BuildForeignResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val senderTopLeaderId: Int,
    val targetCarrierId: Int,
    val ownerId: Int,
    val resourceFactoryInternalData: ResourceFactoryInternalData,
    val qualityLevel: Double,
    val storedFuelRestMass: Double,
    val numBuilding: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Build a foreign "),
            IntString(0),
            NormalString(" factory with quality level "),
            IntString(1),
            NormalString(" owned by player "),
            IntString(2),
            NormalString(" at carrier "),
            IntString(3),
            NormalString(" of player "),
            IntString(4),
            NormalString(". Initial stored fuel rest mass: "),
            IntString(5),
            NormalString(", number of building: "),
            IntString(6),
            NormalString(", output amount: "),
            IntString(7),
            NormalString(", output quality: "),
            IntString(8),
            NormalString(". "),
        ),
        listOf(
            resourceFactoryInternalData.outputResource.toString(),
            qualityLevel.toString(),
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            storedFuelRestMass.toString(),
            numBuilding.toString(),
            (resourceFactoryInternalData.maxOutputAmount * numBuilding).toString(),
            resourceFactoryInternalData.maxOutputResourceQualityData.quality1.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val sameTopLeaderIdI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    NormalString("Top leader id "),
                    IntString(0),
                    NormalString(" is not equal to "),
                    IntString(1),
                    NormalString(". ")
                ),
                listOf(
                    senderTopLeaderId.toString(),
                    playerData.topLeaderId().toString(),
                ),
            )
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowConstruction: Boolean =
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory
        val allowConstructionI18NString: I18NString = if (allowConstruction) {
            I18NString("")
        } else {
            I18NString("Not allow to build factory, not a top leader")
        }

        val validFactoryInternalData: Boolean = resourceFactoryInternalData.squareDiff(
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryInternalData(
                resourceFactoryInternalData.outputResource,
                qualityLevel
            )
        ) < 0.1
        val validFactoryInternalDataI18NString: I18NString = if (validFactoryInternalData) {
            I18NString("")
        } else {
            I18NString("Factory internal data is not valid. ")
        }

        val fuelNeeded: Double =
            storedFuelRestMass + playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                resourceFactoryInternalData.outputResource,
                qualityLevel
            ) * numBuilding
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass
        val hasFuelI18NString: I18NString = if (hasFuel) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }


        return CommandMessage(
            sameTopLeaderId && allowConstruction && validFactoryInternalData && hasFuel,
            listOf(
                sameTopLeaderIdI18NString,
                allowConstructionI18NString,
                validFactoryInternalDataI18NString,
                hasFuelI18NString
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double =
            storedFuelRestMass + playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                resourceFactoryInternalData.outputResource,
                qualityLevel
            ) * numBuilding
        playerData.playerInternalData.physicsData().fuelRestMassData.production -= fuelNeeded + storedFuelRestMass

    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId

        val canForeignInvestorBuild: Boolean = (!sameTopLeaderId &&
                playerData.playerInternalData.politicsData().allowForeignInvestor)

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()

        val canSubordinateBuild: Boolean = (!isSenderTopLeader &&
                playerData.playerInternalData.politicsData().allowSubordinateBuildFactory)

        val allowConstruction: Boolean =
            isSenderTopLeader || canForeignInvestorBuild || canSubordinateBuild

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        return allowConstruction && hasCarrier
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(targetCarrierId)
        carrier.allPopData.labourerPopData.addResourceFactory(
            MutableResourceFactoryData(
                ownerPlayerId = ownerId,
                resourceFactoryInternalData = DataSerializer.copy(resourceFactoryInternalData),
                numBuilding = numBuilding,
                isOpened = true,
                lastOutputAmount = 0.0,
                lastInputResourceMap = mutableMapOf(),
                storedFuelRestMass = storedFuelRestMass,
                lastNumEmployee = 0.0
            )
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Build a fuel factory locally on player
 *
 * @property targetCarrierId build factory on that carrier
 * @property numBuilding number of building in this factory
 */
@Serializable
data class BuildLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val numBuilding: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Build a local fuel factory with quality level at carrier "),
            IntString(0),
            NormalString(" of player "),
            IntString(1),
            NormalString(". Number of building: "),
            IntString(2),
            NormalString(". "),
        ),
        listOf(
            targetCarrierId.toString(),
            toId.toString(),
            numBuilding.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSubordinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        val isSubordinateOrSelfI18NString: I18NString = if (isSubordinateOrSelf) {
            I18NString("")
        } else {
            I18NString("Not subordinate or self.")
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowSubordinateConstruction: Boolean =
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory
        val allowSubordinateConstructionI18NString: I18NString = if (allowSubordinateConstruction) {
            I18NString("")
        } else {
            I18NString("Not allow to build factory, not a top leader")
        }


        return CommandMessage(
            isSubordinateOrSelf && allowSubordinateConstruction,
            listOf(
                isSubordinateOrSelfI18NString,
                allowSubordinateConstructionI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeaderOrSelf: Boolean = playerData.isLeaderOrSelf(fromId)
        val isSelf: Boolean = playerData.playerId == fromId

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()
        val canSenderBuild: Boolean = (isSenderTopLeader ||
                playerData.playerInternalData.politicsData().allowSubordinateBuildFactory)

        val canLeaderBuild: Boolean = (isSelf ||
                playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory)


        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel

        return isLeaderOrSelf && canSenderBuild && canLeaderBuild && hasCarrier && hasFuel
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        val newFuelFactoryInternalData: MutableFuelFactoryInternalData = playerData
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newFuelFactoryInternalData()

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        carrier.allPopData.labourerPopData.addFuelFactory(
            MutableFuelFactoryData(
                ownerPlayerId = toId,
                fuelFactoryInternalData = newFuelFactoryInternalData,
                numBuilding = numBuilding,
                isOpened = true,
                storedFuelRestMass = 0.0,
                lastOutputAmount = 0.0,
                lastNumEmployee = 0.0
            )
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Build a resource factory locally on player
 *
 * @property outputResourceType the resource type of this factory
 * @property targetCarrierId build factory on that carrier
 * @property qualityLevel the quality of the factory, relative to tech level
 * @property numBuilding number of building in this factory
 */
@Serializable
data class BuildLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val outputResourceType: ResourceType,
    val targetCarrierId: Int,
    val qualityLevel: Double,
    val numBuilding: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Build a local "),
            IntString(0),
            NormalString(" factory with quality level "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
            NormalString(". Number of building: "),
            IntString(4),
            NormalString(". "),
        ),
        listOf(
            outputResourceType.toString(),
            qualityLevel.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            numBuilding.toString()
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        val isSubordinateOrSelf: Boolean = playerData.isSubOrdinateOrSelf(toId)
        val isSubordinateOrSelfI18NString: I18NString = if (isSubordinateOrSelf) {
            I18NString("")
        } else {
            I18NString("Not subordinate or self.")
        }

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowSubordinateConstruction: Boolean =
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory
        val allowSubordinateConstructionI18NString: I18NString = if (allowSubordinateConstruction) {
            I18NString("")
        } else {
            I18NString("Not allow to build factory, not a top leader")
        }


        return CommandMessage(
            isSubordinateOrSelf && allowSubordinateConstruction,
            listOf(
                isSubordinateOrSelfI18NString,
                allowSubordinateConstructionI18NString
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeaderOrSelf: Boolean = playerData.isLeaderOrSelf(fromId)
        val isSelf: Boolean = playerData.playerId == fromId

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()
        val canSenderBuild: Boolean = (isSenderTopLeader ||
                playerData.playerInternalData.politicsData().allowSubordinateBuildFactory)

        val canLeaderBuild: Boolean = (isSelf ||
                playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory)


        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                outputResourceType = outputResourceType,
                qualityLevel = qualityLevel
            ) * numBuilding
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel

        return isLeaderOrSelf && canSenderBuild && canLeaderBuild && hasCarrier && hasFuel
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        val newResourceFactoryInternalData: MutableResourceFactoryInternalData =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryInternalData(
                outputResourceType = outputResourceType,
                qualityLevel = qualityLevel
            )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                outputResourceType = outputResourceType,
                qualityLevel = qualityLevel
            ) * numBuilding

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        carrier.allPopData.labourerPopData.addResourceFactory(
            MutableResourceFactoryData(
                ownerPlayerId = toId,
                resourceFactoryInternalData = newResourceFactoryInternalData,
                numBuilding = numBuilding,
                isOpened = true,
                lastOutputAmount = 0.0,
                lastInputResourceMap = mutableMapOf(),
                storedFuelRestMass = 0.0,
                lastNumEmployee = 0.0
            )
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


/**
 * Remove a fuel factory from foreign player
 *
 * @property targetCarrierId remove the factory from that carrier
 * @property targetFuelFactoryId remove the factory with that Id
 */
@Serializable
data class RemoveForeignFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Remove a foreign fuel factory with Id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
        ),
        listOf(
            targetFuelFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        return CommandMessage(true)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasFuelFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
        } else {
            false
        }

        val isOwner: Boolean = if (hasFuelFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
                targetFuelFactoryId
            ).ownerPlayerId == fromId
        } else {
            false
        }

        return hasCarrier && hasFuelFactory && isOwner
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.fuelFactoryMap.remove(targetFuelFactoryId)
    }
}

/**
 * Remove a resource factory from foreign player
 *
 * @property targetCarrierId remove the factory from that carrier
 * @property targetResourceFactoryId remove the factory with that Id
 */
@Serializable
data class RemoveForeignResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Remove a foreign resource factory with Id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
        ),
        listOf(
            targetResourceFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {
        return CommandMessage(true)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasResourceFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                targetResourceFactoryId
            )
        } else {
            false
        }

        val isOwner: Boolean = if (hasResourceFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
                targetResourceFactoryId
            ).ownerPlayerId == fromId
        } else {
            false
        }

        return hasCarrier && hasResourceFactory && isOwner
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.resourceFactoryMap.remove(targetResourceFactoryId)
    }
}


/**
 * Remove a fuel factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetFuelFactoryId remove factory with that id
 */
@Serializable
data class RemoveLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Remove a local factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
        ),
        listOf(
            targetFuelFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)
        val hasCarrierI18NString: I18NString = if (hasCarrier) {
            I18NString("")
        } else {
            I18NString("Carrier does not exist. ")
        }


        val hasFuelFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
        } else {
            false
        }
        val hasFuelFactoryI18NString: I18NString = if (hasFuelFactory) {
            I18NString("")
        } else {
            I18NString("Fuel factory does not exist. ")
        }

        val isRemoveAllowed: Boolean = if (hasFuelFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            val ownerId: Int = carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
                targetFuelFactoryId
            ).ownerPlayerId

            // Allow removal of local factory if the owner is not leader
            // or leader is not allowed to build
            !playerData.isLeader(ownerId) || !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
        } else {
            false
        }
        val isRemoveAllowedI18NString: I18NString = if (isRemoveAllowed) {
            I18NString("")
        } else {
            I18NString("Not allow to remove this factory. ")
        }

        return CommandMessage(
            isSelf && hasCarrier && hasFuelFactory && isRemoveAllowed,
            listOf(
                isSelfI18NString,
                hasCarrierI18NString,
                hasFuelFactoryI18NString,
                isRemoveAllowedI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {

        val isSelf: Boolean = playerData.playerId == toId

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasFuelFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
        } else {
            false
        }

        val isRemoveAllowed: Boolean = if (hasFuelFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            val ownerId: Int = carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
                targetFuelFactoryId
            ).ownerPlayerId

            // Allow removal of local factory if the owner is not leader
            // or leader is not allowed to build
            !playerData.isLeader(ownerId) || !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
        } else {
            false
        }

        return isSelf && hasCarrier && hasFuelFactory && isRemoveAllowed
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.fuelFactoryMap.remove(targetFuelFactoryId)
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Remove a resource factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetResourceFactoryId remove factory with that id
 */
@Serializable
data class RemoveLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Remove a resource factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
        ),
        listOf(
            targetResourceFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)
        val hasCarrierI18NString: I18NString = if (hasCarrier) {
            I18NString("")
        } else {
            I18NString("Carrier does not exist. ")
        }


        val hasResourceFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                targetResourceFactoryId
            )
        } else {
            false
        }
        val hasResourceFactoryI18NString: I18NString = if (hasResourceFactory) {
            I18NString("")
        } else {
            I18NString("Resource factory does not exist. ")
        }

        val isRemoveAllowed: Boolean = if (hasResourceFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            val ownerId: Int = carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
                targetResourceFactoryId
            ).ownerPlayerId

            // Allow removal of local factory if the owner is not leader
            // or leader is not allowed to build
            !playerData.isLeader(ownerId) || !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
        } else {
            false
        }
        val isRemoveAllowedI18NString: I18NString = if (isRemoveAllowed) {
            I18NString("")
        } else {
            I18NString("Not allow to remove this factory. ")
        }

        return CommandMessage(
            isSelf && hasCarrier && hasResourceFactory && isRemoveAllowed,
            listOf(
                isSelfI18NString,
                hasCarrierI18NString,
                hasResourceFactoryI18NString,
                isRemoveAllowedI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {

        val isSelf: Boolean = playerData.playerId == toId

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasResourceFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                targetResourceFactoryId
            )
        } else {
            false
        }

        val isRemoveAllowed: Boolean = if (hasResourceFactory) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            val ownerId: Int = carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
                targetResourceFactoryId
            ).ownerPlayerId

            // Allow removal of local factory if the owner is not leader
            // or leader is not allowed to build
            !playerData.isLeader(ownerId) || !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
        } else {
            false
        }

        return isSelf && hasCarrier && hasResourceFactory && isRemoveAllowed
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.resourceFactoryMap.remove(targetResourceFactoryId)
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Supply fuel to a fuel factory in foreign player
 *
 * @property targetCarrierId supply the factory from that carrier
 * @property targetFuelFactoryId supply the factory with that Id
 * @property amount the amount of fuel supplied to the factory
 */
@Serializable
data class SupplyForeignFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int,
    val amount: Double
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel to the foreign fuel factory with Id "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
        ),
        listOf(
            amount.toString(),
            targetFuelFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= amount
        val hasFuelI18NString: I18NString = if (hasFuel) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }

        return CommandMessage(
            hasFuel,
            listOf(
                hasFuelI18NString
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.physicsData().fuelRestMassData.production -= amount
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasFuelFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
        } else {
            false
        }

        val isFuelIncreaseEnable: Boolean =
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0

        return hasCarrier && hasFuelFactory && isFuelIncreaseEnable
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
            targetFuelFactoryId
        ).storedFuelRestMass += amount
    }
}

/**
 * Supply fuel to a resource factory in foreign player
 *
 * @property targetCarrierId supply the factory from that carrier
 * @property targetResourceFactoryId supply the factory with that Id
 * @property amount the amount of fuel supplied to the factory
 */
@Serializable
data class SupplyForeignResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int,
    val amount: Double
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel to the foreign resource factory with Id "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
        ),
        listOf(
            amount.toString(),
            targetResourceFactoryId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandMessage {

        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= amount
        val hasFuelI18NString: I18NString = if (hasFuel) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }

        return CommandMessage(
            hasFuel,
            listOf(
                hasFuelI18NString
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.physicsData().fuelRestMassData.production -= amount
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val hasResourceFactory: Boolean = if (hasCarrier) {
            val carrier: MutableCarrierData =
                playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                    targetCarrierId
                )

            carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                targetResourceFactoryId
            )
        } else {
            false
        }

        val isFuelIncreaseEnable: Boolean =
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0

        return hasCarrier && hasResourceFactory && isFuelIncreaseEnable
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
            targetResourceFactoryId
        ).storedFuelRestMass += amount
    }
}