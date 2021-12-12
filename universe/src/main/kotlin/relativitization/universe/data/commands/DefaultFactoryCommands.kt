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
    ): CommandErrorMessage {
        val sameTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == senderTopLeaderId,
            CommandI18NStringFactory.isTopLeaderIdWrong(senderTopLeaderId, playerData.topLeaderId())
        )

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowConstruction = CommandErrorMessage(
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Not allow to build factory. ")
        )

        val validFactoryInternalData = CommandErrorMessage(
            fuelFactoryInternalData.squareDiff(
                playerData.playerInternalData.playerScienceData()
                    .playerScienceApplicationData.newFuelFactoryInternalData()
            ) < 0.1,
            I18NString("Factory internal data is not valid. ")
        )

        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass,
            I18NString("Not enough fuel rest mass. ")
        )


        return CommandErrorMessage(
            listOf(
                sameTopLeaderId,
                allowConstruction,
                validFactoryInternalData,
                hasFuel,
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

        val allowConstruction = CommandErrorMessage(
            isSenderTopLeader || canForeignInvestorBuild || canSubordinateBuild,
            I18NString("Not allow to build factory. ")
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                allowConstruction,
                hasCarrier,
            )
        ).success
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
    ): CommandErrorMessage {
        val sameTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == senderTopLeaderId,
            CommandI18NStringFactory.isTopLeaderIdWrong(senderTopLeaderId, playerData.topLeaderId())
        )

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowConstruction = CommandErrorMessage(
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Not allow to build factory. ")
        )

        val validFactoryInternalData = CommandErrorMessage(
            resourceFactoryInternalData.squareDiff(
                playerData.playerInternalData.playerScienceData().playerScienceApplicationData
                    .newResourceFactoryInternalData(
                        resourceFactoryInternalData.outputResource,
                        qualityLevel
                    )
            ) < 0.1,
            I18NString("Factory internal data is not valid. ")
        )

        val fuelNeeded: Double =
            storedFuelRestMass + playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                resourceFactoryInternalData.outputResource,
                qualityLevel
            ) * numBuilding
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass,
            I18NString("Not enough fuel rest mass. ")
        )


        return CommandErrorMessage(
            listOf(
                sameTopLeaderId,
                allowConstruction,
                validFactoryInternalData,
                hasFuel,
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

        val allowConstruction = CommandErrorMessage(
            isSenderTopLeader || canForeignInvestorBuild || canSubordinateBuild,
            I18NString("Not allow to build factory. ")
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                allowConstruction,
                hasCarrier,
            )
        ).success
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
    ): CommandErrorMessage {
        val isSubordinateOrSelf = CommandErrorMessage(
            playerData.isSubOrdinateOrSelf(toId),
            I18NString("Not subordinate or self.")
        )

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowSubordinateConstruction = CommandErrorMessage(
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Not allow to build factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSubordinateOrSelf,
                allowSubordinateConstruction,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeaderOrSelf = CommandErrorMessage(
            playerData.isLeaderOrSelf(fromId),
            I18NString("Sender is not leader or self. ")
        )

        val isSelf: Boolean = playerData.playerId == fromId

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()

        val canSenderBuild = CommandErrorMessage(
            isSenderTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Sender cannot build. ")
        )

        val canLeaderBuild = CommandErrorMessage(
            isSelf || playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory,
            I18NString("Sender is not a top leader. ")
        )


        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction() * numBuilding

        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= requiredFuel,
            I18NString("Not enough fuel. ")
        )

        return CommandErrorMessage(
            listOf(
                isLeaderOrSelf,
                canSenderBuild,
                canLeaderBuild,
                hasCarrier,
                hasFuel
            )
        ).success
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
    ): CommandErrorMessage {
        val isSubordinateOrSelf = CommandErrorMessage(
            playerData.isSubOrdinateOrSelf(toId),
            I18NString("Not subordinate or self.")
        )

        val isTopLeader: Boolean = playerData.isTopLeader()
        val allowSubordinateConstruction = CommandErrorMessage(
            isTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Not allow to build factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSubordinateOrSelf,
                allowSubordinateConstruction,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isLeaderOrSelf = CommandErrorMessage(
            playerData.isLeaderOrSelf(fromId),
            I18NString("Sender is not leader or self. ")
        )

        val isSelf: Boolean = playerData.playerId == fromId

        val isSenderTopLeader: Boolean = fromId == playerData.topLeaderId()

        val canSenderBuild = CommandErrorMessage(
            isSenderTopLeader || playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            I18NString("Sender cannot build. ")
        )

        val canLeaderBuild = CommandErrorMessage(
            isSelf || playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory,
            I18NString("Sender is not a top leader. ")
        )


        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val requiredFuel: Double =
            playerData.playerInternalData.playerScienceData().playerScienceApplicationData.newResourceFactoryFuelNeededByConstruction(
                outputResourceType = outputResourceType,
                qualityLevel = qualityLevel
            ) * numBuilding

        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production > -requiredFuel,
            I18NString("Not enough fuel. ")
        )

        return CommandErrorMessage(
            listOf(
                isLeaderOrSelf,
                canSenderBuild,
                canLeaderBuild,
                hasCarrier,
                hasFuel
            )
        ).success
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
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasFuelFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        val isOwner = CommandErrorMessage(
            if (hasFuelFactory.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
                    targetFuelFactoryId
                ).ownerPlayerId == fromId
            } else {
                false
            },
            I18NString("Is not owner of this factory. ")
        )

        return CommandErrorMessage(
            listOf(
                hasCarrier,
                hasFuelFactory,
                isOwner,
            )
        ).success
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
    ): CommandErrorMessage {
        return CommandErrorMessage(true)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasResourceFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                    targetResourceFactoryId
                )
            } else {
                false
            },
            I18NString("Resource factory does not exist. ")
        )

        val isOwner = CommandErrorMessage(
            if (hasResourceFactory.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
                    targetResourceFactoryId
                ).ownerPlayerId == fromId
            } else {
                false
            },
            I18NString("Is not owner of this factory. ")
        )

        return CommandErrorMessage(
            listOf(
                hasCarrier,
                hasResourceFactory,
                isOwner,
            )
        ).success
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
    ): CommandErrorMessage {

        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasFuelFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        val isRemoveAllowed = CommandErrorMessage(
            if (hasFuelFactory.success) {
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
            },
            I18NString("Not allow to remove this factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
                isRemoveAllowed,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasFuelFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        val isRemoveAllowed = CommandErrorMessage(
            if (hasFuelFactory.success) {
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
            },
            I18NString("Not allow to remove this local factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
                isRemoveAllowed,
            )
        ).success
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
    ): CommandErrorMessage {

        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasResourceFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                    targetResourceFactoryId
                )
            } else {
                false
            },
            I18NString("Resource factory does not exist. ")
        )

        val isRemoveAllowed = CommandErrorMessage(
            if (hasResourceFactory.success) {
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
            },
            I18NString("Not allow to remove this factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
                isRemoveAllowed,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasResourceFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                    targetResourceFactoryId
                )
            } else {
                false
            },
            I18NString("Resource factory does not exist. ")
        )

        val isRemoveAllowed = CommandErrorMessage(
            if (hasResourceFactory.success) {
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
            },
            I18NString("Not allow to remove this local factory. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
                isRemoveAllowed,
            )
        ).success
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
    ): CommandErrorMessage {

        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= amount,
            I18NString("Not enough fuel rest mass. ")
        )

        return CommandErrorMessage(
            listOf(
                hasFuel
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

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasFuelFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(targetFuelFactoryId)
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                hasCarrier,
                hasFuelFactory,
                isFuelIncreaseEnable,
            )
        ).success
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
    ): CommandErrorMessage {

        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= amount,
            I18NString("Not enough fuel rest mass. ")
        )

        return CommandErrorMessage(
            listOf(
                hasFuel
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
        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(targetCarrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasResourceFactory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                        targetCarrierId
                    )

                carrier.allPopData.labourerPopData.resourceFactoryMap.containsKey(
                    targetResourceFactoryId
                )
            } else {
                false
            },
            I18NString("Resource factory does not exist. ")
        )


        val isFuelIncreaseEnable = CommandErrorMessage(
            playerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0,
            I18NString("Fuel increase is disabled. ")
        )

        return CommandErrorMessage(
            listOf(
                hasCarrier,
                hasResourceFactory,
                isFuelIncreaseEnable,
            )
        ).success
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