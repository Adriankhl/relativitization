package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.defaults.physics.Int4D
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
    val xCor: Double = 0.0,
    val yCor: Double = 0.0,
    val range: Double = 1.0,
    val researchEquipmentPerTime: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
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
            xCor.toString(),
            yCor.toString(),
            range.toString(),
            researchEquipmentPerTime.toString(),
            maxNumEmployee.toString(),
        ),
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