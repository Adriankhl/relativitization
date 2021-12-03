package relativitization.universe.mechanisms.defaults.regular.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendFuelCommand
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

        val fuelRestMass: Double =
            mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass

        // Repair empty leader list
        if (mutablePlayerData.playerInternalData.leaderIdList.isEmpty()) {
            mutablePlayerData.playerInternalData.leaderIdList.add(mutablePlayerData.playerId)
        }

        val numLeader: Int = mutablePlayerData.playerInternalData.leaderIdList.size

        val fractionList: List<Double> = Fraction.oneFractionList(numLeader, fraction).reversed()

        // Send fuel command
        val commandList: List<Command> =
            mutablePlayerData.playerInternalData.leaderIdList.mapIndexed { index, id ->
                SendFuelCommand(
                    toId = id,
                    fromId = universeData3DAtPlayer.getCurrentPlayerData().playerId,
                    fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D,
                    amount = fuelRestMass * fractionList[index],
                    senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceApplicationData.fuelLogisticsLossFractionPerDistance,
                )
            }

        // Clear stored fuel in tax

        return commandList
    }
}