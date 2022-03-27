package relativitization.universe.mechanisms.defaults.dilated.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendFuelCommand
import relativitization.universe.data.components.economyData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.collection.Fraction
import relativitization.universe.mechanisms.Mechanism

object SendTax : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val fraction: Double = 0.5

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
                    fromId = universeData3DAtPlayer.getCurrentPlayerData().playerId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D,
                    amount = fuelRestMassToSend * fractionList[index],
                    senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData()
                        .playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
                )
            }

        return commandList
    }
}