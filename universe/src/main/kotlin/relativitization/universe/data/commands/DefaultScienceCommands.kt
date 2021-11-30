package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.InstituteInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString

/**
 * Build a research institute
 */
@Serializable
data class BuildInstituteCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val carrierId: Int,
    val instituteInternalData: InstituteInternalData,
) : DefaultCommand() {
    override val description: I18NString = I18NString(
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
    ): CanSendCheckMessage {
        val isSelf: Boolean = playerData.playerId == toId
        val isSelfI18NString: I18NString = if (isSelf) {
            I18NString("")
        } else {
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        }

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)
        val hasCarrierI18NString: I18NString = if (hasCarrier) {
            I18NString("")
        } else {
            I18NString("Carrier does not exist. ")
        }

        val isRangeValid: Boolean = instituteInternalData.range >= 0.25
        val isRangeValidI18NString: I18NString = if (isRangeValid) {
            I18NString("")
        } else {
            I18NString("Range should be smaller than 0.25")
        }

        return CanSendCheckMessage(
            isSelf && hasCarrier && isRangeValid,
            listOf(
                isSelfI18NString,
                hasCarrierI18NString,
                isRangeValidI18NString,
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        val isSelf: Boolean = playerData.playerId == fromId

        val hasCarrier: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.containsKey(carrierId)

        return isSelf && hasCarrier
    }

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        playerData.playerInternalData.popSystemData().carrierDataMap.getValue(
            carrierId
        ).allPopData.scholarPopData.addInstitute(
            MutableInstituteData(
                instituteInternalData = DataSerializer.copy(instituteInternalData),
                strength = 0.0,
                reputation = 0.0,
                lastNumEmployee = 0.0,
                size = 0.0
            )
        )
    }
}