package relativitization.universe.game.mechanisms.defaults.dilated.logistics

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.commands.SendFuelCommand
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.maths.collection.Fraction
import relativitization.universe.core.mechanisms.Mechanism
import kotlin.random.Random

object SendTax : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        val fraction = 0.5

        val fuelRestMassToSend: Double =
            mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass

        // Clear stored fuel in tax
        mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass = 0.0

        val numTaxReceiver: Int = mutablePlayerData.getLeaderAndSelfIdList().size

        val fractionList: List<Double> = Fraction.oneFractionList(
            numTaxReceiver,
            fraction
        ).reversed()

        // Send fuel command
        val commandList: List<Command> =
            mutablePlayerData.getLeaderAndSelfIdList().mapIndexed { index, id ->
                SendFuelCommand(
                    toId = id,
                    amount = fuelRestMassToSend * fractionList[index],
                    senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData()
                        .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
                )
            }

        return commandList
    }
}