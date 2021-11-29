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
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Build a fuel factory on player
 *
 * @property senderTopLeaderId the player id of the top leader of the sender
 * @property targetCarrierId build factory on that carrier
 * @property ownerId who own this factory
 * @property fuelFactoryInternalData data of the factory
 * @property qualityLevel the quality of the factory, relative to tech level
 * @property storedFuelRestMass fuel stored in the newly built factory
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
    val qualityLevel: Double,
    val storedFuelRestMass: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a foreign fuel factory with quality level "),
            IntString(0),
            RealString(" owned by "),
            IntString(1),
            RealString(" at carrier "),
            IntString(2),
            RealString(" of player "),
            IntString(3),
        ),
        listOf(
            qualityLevel.toString(),
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val sameTopLeaderIdI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Top leader id "),
                    IntString(0),
                    RealString(" is not equal to "),
                    IntString(1),
                    RealString(". ")
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
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryInternalData(
                qualityLevel
            )
        ) < 0.1
        val validFactoryInternalDataI18NString: I18NString = if (validFactoryInternalData) {
            I18NString("")
        } else {
            I18NString("Factory internal data is not valid. ")
        }

        val fuelNeeded: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(
                qualityLevel
            )
        val enoughFuelRestMass: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass
        val enoughFuelRestMassI18NString: I18NString = if (enoughFuelRestMass) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }


        return CanSendCheckMessage(
            sameTopLeaderId && allowConstruction && validFactoryInternalData && enoughFuelRestMass,
            I18NString.combine(
                listOf(
                    sameTopLeaderIdI18NString,
                    allowConstructionI18NString,
                    validFactoryInternalDataI18NString,
                    enoughFuelRestMassI18NString
                )
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(
                qualityLevel
            )
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
                numBuilding = 1.0,
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
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a foreign resource factory with quality level "),
            IntString(0),
            RealString(" owned by "),
            IntString(1),
            RealString(" at carrier "),
            IntString(2),
            RealString(" of player "),
            IntString(3),
            RealString(". Initial stored fuel rest mass: "),
            IntString(4),
        ),
        listOf(
            qualityLevel.toString(),
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            storedFuelRestMass.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        val sameTopLeaderId: Boolean = playerData.topLeaderId() == senderTopLeaderId
        val sameTopLeaderIdI18NString: I18NString = if (sameTopLeaderId) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Top leader id "),
                    IntString(0),
                    RealString(" is not equal to "),
                    IntString(1),
                    RealString(". ")
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
            )
        val enoughFuelRestMass: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass
        val enoughFuelRestMassI18NString: I18NString = if (enoughFuelRestMass) {
            I18NString("")
        } else {
            I18NString("Not enough fuel rest mass. ")
        }


        return CanSendCheckMessage(
            sameTopLeaderId && allowConstruction && validFactoryInternalData && enoughFuelRestMass,
            I18NString.combine(
                listOf(
                    sameTopLeaderIdI18NString,
                    allowConstructionI18NString,
                    validFactoryInternalDataI18NString,
                    enoughFuelRestMassI18NString
                )
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
            )
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
                numBuilding = 1.0,
                isOpened = true,
                lastOutputAmount = 0.0,
                lastInputAmountMap = mutableMapOf(),
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
 * @property qualityLevel the quality of the factory, relative to tech level
 */
@Serializable
data class BuildLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val qualityLevel: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a local factory with quality level "),
            IntString(0),
            RealString(" at carrier "),
            IntString(1),
            RealString(" of player "),
            IntString(2),
        ),
        listOf(
            qualityLevel.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
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


        return CanSendCheckMessage(
            isSubordinateOrSelf && allowSubordinateConstruction,
            I18NString.combine(
                listOf(
                    isSubordinateOrSelfI18NString,
                    allowSubordinateConstructionI18NString
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeader: Boolean = playerData.isLeaderOrSelf(fromId)
        val isSelf: Boolean = playerData.playerId == fromId

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()
        val canSenderBuild: Boolean = (isSenderTopLeader ||
                playerData.playerInternalData.politicsData().allowSubordinateBuildFactory)

        val canLeaderBuild: Boolean = (isSelf ||
                playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory)


        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId)

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel

        return isLeader && canSenderBuild && canLeaderBuild && hasCarrier && hasFuel
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        val newFuelFactoryInternalData: MutableFuelFactoryInternalData =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryInternalData(
                qualityLevel = qualityLevel
            )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(
                qualityLevel = qualityLevel
            )

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        carrier.allPopData.labourerPopData.addFuelFactory(
            MutableFuelFactoryData(
                ownerPlayerId = toId,
                fuelFactoryInternalData = newFuelFactoryInternalData,
                numBuilding = 1.0,
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
 */
@Serializable
data class BuildLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val outputResourceType: ResourceType,
    val targetCarrierId: Int,
    val qualityLevel: Double,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
        listOf(
            RealString("Build a local factory with quality level "),
            IntString(0),
            RealString(" at carrier "),
            IntString(1),
            RealString(" of player "),
            IntString(2),
        ),
        listOf(
            qualityLevel.toString(),
            targetCarrierId.toString(),
            toId.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
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


        return CanSendCheckMessage(
            isSubordinateOrSelf && allowSubordinateConstruction,
            I18NString.combine(
                listOf(
                    isSubordinateOrSelfI18NString,
                    allowSubordinateConstructionI18NString
                )
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeader: Boolean = playerData.isLeaderOrSelf(fromId)
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
            )
        val hasFuel: Boolean =
            playerData.playerInternalData.physicsData().fuelRestMassData.production > requiredFuel

        return isLeader && canSenderBuild && canLeaderBuild && hasCarrier && hasFuel
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
            )

        playerData.playerInternalData.physicsData().fuelRestMassData.production -= requiredFuel

        carrier.allPopData.labourerPopData.addResourceFactory(
            MutableResourceFactoryData(
                ownerPlayerId = toId,
                resourceFactoryInternalData = newResourceFactoryInternalData,
                numBuilding = 1.0,
                isOpened = true,
                lastOutputAmount = 0.0,
                lastInputAmountMap = mutableMapOf(),
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
 * Remove a fuel factory on foreign player
 *
 * @property targetCarrierId remove the factory on that carrier
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
            RealString("Remove a local factory with Id "),
            IntString(0),
            RealString(" at carrier "),
            IntString(1),
            RealString(" of player "),
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
    ): CanSendCheckMessage {
        TODO("Not yet implemented")
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        TODO("Not yet implemented")
    }

}