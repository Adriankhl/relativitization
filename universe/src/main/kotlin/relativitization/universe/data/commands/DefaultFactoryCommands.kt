package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.*
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
 * @property maxNumEmployee maximum number of employee
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
    val maxNumEmployee: Double,
    val storedFuelRestMass: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Build a foreign fuel factory owned by player "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". Max. employee: "),
            IntString(3),
            NormalString(", max. output: "),
            IntString(4),
            NormalString(", initial stored fuel rest mass: "),
            IntString(5),
            NormalString(". "),
        ),
        listOf(
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            maxNumEmployee.toString(),
            (maxNumEmployee * fuelFactoryInternalData.maxOutputAmountPerEmployee).toString(),
            storedFuelRestMass.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val sameTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == senderTopLeaderId,
            CommandI18NStringFactory.isTopLeaderIdWrong(playerData.topLeaderId(), senderTopLeaderId)
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
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(maxNumEmployee)
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass,
            I18NString("Not enough fuel rest mass. ")
        )

        val isMaxNumEmployeeValid = CommandErrorMessage(
            maxNumEmployee > 1.0,
            I18NString("Number of employee should be >= 1. ")
        )


        return CommandErrorMessage(
            listOf(
                sameTopLeaderId,
                allowConstruction,
                validFactoryInternalData,
                hasFuel,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData()
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(maxNumEmployee)

        playerData.playerInternalData.physicsData().removeExternalProductionFuel(fuelNeeded + storedFuelRestMass)

    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(targetCarrierId)

        carrier.allPopData.labourerPopData.addFuelFactory(
            MutableFuelFactoryData(
                ownerPlayerId = ownerId,
                fuelFactoryInternalData = DataSerializer.copy(fuelFactoryInternalData),
                maxNumEmployee = maxNumEmployee,
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
 * @property maxNumEmployee maximum number of employee
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
    val maxNumEmployee: Double,
    val storedFuelRestMass: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
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
            NormalString(". Max. number of employee: "),
            IntString(5),
            NormalString(", output amount: "),
            IntString(6),
            NormalString(", output quality: "),
            IntString(7),
            NormalString(", initial stored fuel rest mass: "),
            IntString(8),
            NormalString(". "),
        ),
        listOf(
            resourceFactoryInternalData.outputResource.toString(),
            qualityLevel.toString(),
            ownerId.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            maxNumEmployee.toString(),
            (resourceFactoryInternalData.maxOutputAmountPerEmployee * maxNumEmployee).toString(),
            resourceFactoryInternalData.maxOutputResourceQualityData.quality1.toString(),
            storedFuelRestMass.toString(),
        )
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val sameTopLeaderId = CommandErrorMessage(
            playerData.topLeaderId() == senderTopLeaderId,
            CommandI18NStringFactory.isTopLeaderIdWrong(playerData.topLeaderId(), senderTopLeaderId)
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

        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = resourceFactoryInternalData.outputResource,
                maxNumEmployee = maxNumEmployee,
                qualityLevel = qualityLevel,
            )
        val hasFuel = CommandErrorMessage(
            playerData.playerInternalData.physicsData().fuelRestMassData.production >= fuelNeeded + storedFuelRestMass,
            I18NString("Not enough fuel rest mass. ")
        )

        val isMaxNumEmployeeValid = CommandErrorMessage(
            maxNumEmployee >= 1.0,
            I18NString("Max. number of employee should be >= 1. ")
        )

        return CommandErrorMessage(
            listOf(
                sameTopLeaderId,
                allowConstruction,
                validFactoryInternalData,
                hasFuel,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {
        val fuelNeeded: Double = playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = resourceFactoryInternalData.outputResource,
                maxNumEmployee = maxNumEmployee,
                qualityLevel = qualityLevel,
            )
        playerData.playerInternalData.physicsData().removeExternalProductionFuel(fuelNeeded + storedFuelRestMass)

    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData = playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            targetCarrierId
        )

        carrier.allPopData.labourerPopData.addResourceFactory(
            MutableResourceFactoryData(
                ownerPlayerId = ownerId,
                resourceFactoryInternalData = DataSerializer.copy(resourceFactoryInternalData),
                maxNumEmployee = maxNumEmployee,
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
 * @property maxNumEmployee maximum number of employee
 */
@Serializable
data class BuildLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val maxNumEmployee: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Build a local fuel factory with quality level at carrier "),
            IntString(0),
            NormalString(" of player "),
            IntString(1),
            NormalString(". Max. number of employee: "),
            IntString(2),
            NormalString(". "),
        ),
        listOf(
            targetCarrierId.toString(),
            toId.toString(),
            maxNumEmployee.toString(),
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

        val isMaxNumEmployeeValid = CommandErrorMessage(
            maxNumEmployee >= 1.0,
            I18NString("Max. number of employee should be >= 1. ")
        )

        return CommandErrorMessage(
            listOf(
                isSubordinateOrSelf,
                allowSubordinateConstruction,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(maxNumEmployee)

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
        )
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
            .playerScienceApplicationData.newFuelFactoryFuelNeededByConstruction(maxNumEmployee)

        playerData.playerInternalData.physicsData().removeExternalProductionFuel(requiredFuel)

        carrier.allPopData.labourerPopData.addFuelFactory(
            MutableFuelFactoryData(
                ownerPlayerId = toId,
                fuelFactoryInternalData = newFuelFactoryInternalData,
                maxNumEmployee = maxNumEmployee,
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
 * @property maxNumEmployee maximum number of employee
 */
@Serializable
data class BuildLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val outputResourceType: ResourceType,
    val targetCarrierId: Int,
    val qualityLevel: Double,
    val maxNumEmployee: Double,
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Build a local "),
            IntString(0),
            NormalString(" factory with quality level "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
            NormalString(". Max. number of employee: "),
            IntString(4),
            NormalString(". "),
        ),
        listOf(
            outputResourceType.toString(),
            qualityLevel.toString(),
            targetCarrierId.toString(),
            toId.toString(),
            maxNumEmployee.toString(),
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

        val isMaxNumEmployeeValid = CommandErrorMessage(
            maxNumEmployee >= 1.0,
            I18NString("Max. number of employee should be >= 1. ")
        )

        return CommandErrorMessage(
            listOf(
                isSubordinateOrSelf,
                allowSubordinateConstruction,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = outputResourceType,
                maxNumEmployee = maxNumEmployee,
                qualityLevel = qualityLevel
            )

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
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {

        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        val newResourceFactoryInternalData: MutableResourceFactoryInternalData = playerData.playerInternalData
            .playerScienceData().playerScienceApplicationData.newResourceFactoryInternalData(
                outputResourceType = outputResourceType,
                qualityLevel = qualityLevel
            )

        val requiredFuel: Double = playerData.playerInternalData.playerScienceData().playerScienceApplicationData
            .newResourceFactoryFuelNeededByConstruction(
                outputResourceType = outputResourceType,
                maxNumEmployee = maxNumEmployee,
                qualityLevel = qualityLevel
            )

        playerData.playerInternalData.physicsData().removeExternalProductionFuel(requiredFuel)

        carrier.allPopData.labourerPopData.addResourceFactory(
            MutableResourceFactoryData(
                ownerPlayerId = toId,
                resourceFactoryInternalData = newResourceFactoryInternalData,
                maxNumEmployee = maxNumEmployee,
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
 * @property targetFuelFactoryId remove the factory with that ID
 */
@Serializable
data class RemoveForeignFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Remove a foreign fuel factory with Id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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
    ): CommandErrorMessage {
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
        )
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
 * @property targetResourceFactoryId remove the factory with that ID
 */
@Serializable
data class RemoveForeignResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Remove a foreign resource factory with Id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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
    ): CommandErrorMessage {
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
        )
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
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Remove a local factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

                // Allow removal of local factory if the player is the owner,
                // the owner is not a leader, or leader building is not allowed
                (ownerId == playerData.playerId) ||
                        !playerData.isLeader(ownerId) ||
                        !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
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
    ): CommandErrorMessage {
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

                // Allow removal of local factory if the player is the owner,
                // the owner is not a leader, or leader building is not allowed
                (ownerId == playerData.playerId) ||
                        !playerData.isLeader(ownerId) ||
                        !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
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
        )
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
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Remove a resource factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

                // Allow removal of local factory if the player is the owner,
                // the owner is not a leader, or leader building is not allowed
                (ownerId == playerData.playerId) ||
                        !playerData.isLeader(ownerId) ||
                        !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
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
    ): CommandErrorMessage {
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

                // Allow removal of local factory if the player is the owner,
                // the owner is not a leader, or leader building is not allowed
                (ownerId == playerData.playerId) ||
                        !playerData.isLeader(ownerId) ||
                        !playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory
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
        )
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
 * @property targetFuelFactoryId supply the factory with that ID
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
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel to the foreign fuel factory with Id "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
            NormalString(". "),
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
        playerData.playerInternalData.physicsData().removeExternalProductionFuel(amount)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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
        )
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
 * Open a fuel factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetFuelFactoryId remove factory with that id
 */
@Serializable
data class OpenLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Open a fuel factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(
                    targetFuelFactoryId
                )
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(
                    targetFuelFactoryId
                )
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
            targetFuelFactoryId
        ).isOpened = true
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Close a fuel factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetFuelFactoryId remove factory with that id
 */
@Serializable
data class CloseLocalFuelFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetFuelFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Close a fuel factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(
                    targetFuelFactoryId
                )
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

                carrier.allPopData.labourerPopData.fuelFactoryMap.containsKey(
                    targetFuelFactoryId
                )
            } else {
                false
            },
            I18NString("Fuel factory does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasFuelFactory,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.fuelFactoryMap.getValue(
            targetFuelFactoryId
        ).isOpened = false
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Supply fuel to a resource factory in foreign player
 *
 * @property targetCarrierId supply the factory from that carrier
 * @property targetResourceFactoryId supply the factory with that ID
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
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Send "),
            IntString(0),
            NormalString(" fuel to the foreign resource factory with Id "),
            IntString(1),
            NormalString(" at carrier "),
            IntString(2),
            NormalString(" of player "),
            IntString(3),
            NormalString(". "),
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
        playerData.playerInternalData.physicsData().removeExternalProductionFuel(amount)
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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
        )
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

/**
 * Open a resource factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetResourceFactoryId remove factory with that id
 */
@Serializable
data class OpenLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Open a resource factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
            targetResourceFactoryId
        ).isOpened = true
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Open a resource factory locally from player
 *
 * @property targetCarrierId remove factory from that carrier
 * @property targetResourceFactoryId remove factory with that id
 */
@Serializable
data class CloseLocalResourceFactoryCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val targetCarrierId: Int,
    val targetResourceFactoryId: Int
) : DefaultCommand() {
    override fun description(): I18NString = I18NString(
        listOf(
            NormalString("Close a resource factory with id "),
            IntString(0),
            NormalString(" at carrier "),
            IntString(1),
            NormalString(" of player "),
            IntString(2),
            NormalString(". "),
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

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
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

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasResourceFactory,
            )
        )
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val carrier: MutableCarrierData =
            playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                targetCarrierId
            )

        carrier.allPopData.labourerPopData.resourceFactoryMap.getValue(
            targetResourceFactoryId
        ).isOpened = false
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}