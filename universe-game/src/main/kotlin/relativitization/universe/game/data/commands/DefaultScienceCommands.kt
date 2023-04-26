package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.InstituteInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.game.data.components.popSystemData

/**
 * Build a research institute
 *
 * @property carrierId the id of the carrier to build this institute
 * @property instituteInternalData the data describing this institute
 */
@Serializable
data class BuildInstituteCommand(
    override val toId: Int,
    val carrierId: Int,
    val instituteInternalData: InstituteInternalData,
) : DefaultCommand() {
    override fun name(): String = "Build Institute"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Build an institute in carrier "),
            IntString(0),
            NormalString(": knowledge plane position ("),
            IntString(1),
            NormalString(", "),
            IntString(2),
            NormalString("), range "),
            IntString(3),
            NormalString(", research equipment per time "),
            IntString(4),
            NormalString(", max employee "),
            IntString(5),
            NormalString(". "),
        ),
        listOf(
            carrierId.toString(),
            instituteInternalData.xCor.toString(),
            instituteInternalData.yCor.toString(),
            instituteInternalData.range.toString(),
            instituteInternalData.researchEquipmentPerTime.toString(),
            instituteInternalData.maxNumEmployee.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val isRangeValid = CommandErrorMessage(
            instituteInternalData.range >= 0.25,
            I18NString("Range should be >= 0.25")
        )

        val isResearchEquipmentPerTimeValid = CommandErrorMessage(
            instituteInternalData.researchEquipmentPerTime >= 0.0,
            I18NString("Research equipment per time should be >= 0.0. ")
        )

        val isMaxNumEmployeeValid = CommandErrorMessage(
            instituteInternalData.maxNumEmployee >= 0.0,
            I18NString("Max employee should be >= 0.0. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                isRangeValid,
                isResearchEquipmentPerTimeValid,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.scholarPopData.addInstitute(
            MutableInstituteData(
                instituteInternalData = DataSerializer.copy(instituteInternalData),
                strength = 0.0,
                reputation = 0.0,
                lastNumEmployee = 0.0,
            )
        )
    }
}

/**
 * Build a research laboratory
 *
 * @property carrierId the id of the carrier building this laboratory
 * @property laboratoryInternalData the data describing this laboratory
 */
@Serializable
data class BuildLaboratoryCommand(
    override val toId: Int,
    val carrierId: Int,
    val laboratoryInternalData: LaboratoryInternalData,
) : DefaultCommand() {
    override fun name(): String = "Build Laboratory"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Build a laboratory in carrier "),
            IntString(0),
            NormalString(": knowledge plane position ("),
            IntString(1),
            NormalString(", "),
            IntString(2),
            NormalString("), range "),
            IntString(3),
            NormalString(", research equipment per time "),
            IntString(4),
            NormalString(", max employee "),
            IntString(5),
            NormalString(". "),
        ),
        listOf(
            carrierId.toString(),
            laboratoryInternalData.xCor.toString(),
            laboratoryInternalData.yCor.toString(),
            laboratoryInternalData.range.toString(),
            laboratoryInternalData.researchEquipmentPerTime.toString(),
            laboratoryInternalData.maxNumEmployee.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val isRangeValid = CommandErrorMessage(
            laboratoryInternalData.range >= 0.25,
            I18NString("Range should be >= 0.25")
        )

        val isResearchEquipmentPerTimeValid = CommandErrorMessage(
            laboratoryInternalData.researchEquipmentPerTime >= 0.0,
            I18NString("Research equipment per time should be >= 0.0. ")
        )

        val isMaxNumEmployeeValid = CommandErrorMessage(
            laboratoryInternalData.maxNumEmployee >= 0.0,
            I18NString("Max employee should be >= 0.0. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                isRangeValid,
                isResearchEquipmentPerTimeValid,
                isMaxNumEmployeeValid,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.engineerPopData.addLaboratory(
            MutableLaboratoryData(
                laboratoryInternalData = DataSerializer.copy(laboratoryInternalData),
                strength = 0.0,
                reputation = 0.0,
                lastNumEmployee = 0.0,
            )
        )
    }
}

/**
 * Remove a research institute
 *
 * @property carrierId the id of the carrier where this institute is located at
 * @property instituteId the id of this institute
 */
@Serializable
data class RemoveInstituteCommand(
    override val toId: Int,
    val carrierId: Int,
    val instituteId: Int,
) : DefaultCommand() {
    override fun name(): String = "Remove Institute"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Remove institute "),
            IntString(0),
            NormalString(" in carrier "),
            IntString(1),
            NormalString(". "),
        ),
        listOf(
            instituteId.toString(),
            carrierId.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasInstitute = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
                carrier.allPopData.scholarPopData.instituteMap.containsKey(instituteId)
            } else {
                false
            },
            I18NString("Institute does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasInstitute
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasInstitute = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
                carrier.allPopData.scholarPopData.instituteMap.containsKey(instituteId)
            } else {
                false
            },
            I18NString("Institute does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasInstitute,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.scholarPopData.instituteMap.remove(instituteId)
    }
}

/**
 * Remove a research laboratory
 *
 * @property carrierId the id of the carrier where this institute is located at
 * @property laboratoryId the id of this laboratory
 */
@Serializable
data class RemoveLaboratoryCommand(
    override val toId: Int,
    val carrierId: Int,
    val laboratoryId: Int,
) : DefaultCommand() {
    override fun name(): String = "Remove Laboratory"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Remove laboratory "),
            IntString(0),
            NormalString(" in carrier "),
            IntString(1),
            NormalString(". "),
        ),
        listOf(
            laboratoryId.toString(),
            carrierId.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasLaboratory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
                carrier.allPopData.engineerPopData.laboratoryMap.containsKey(laboratoryId)
            } else {
                false
            },
            I18NString("Laboratory does not exist. ")
        )


        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasLaboratory,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        val hasCarrier = CommandErrorMessage(
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId),
            I18NString("Carrier does not exist. ")
        )

        val hasLaboratory = CommandErrorMessage(
            if (hasCarrier.success) {
                val carrier: MutableCarrierData =
                    playerData.playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)
                carrier.allPopData.engineerPopData.laboratoryMap.containsKey(laboratoryId)
            } else {
                false
            },
            I18NString("Laboratory does not exist. ")
        )

        return CommandErrorMessage(
            listOf(
                isSelf,
                hasCarrier,
                hasLaboratory,
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.engineerPopData.laboratoryMap.remove(laboratoryId)
    }
}