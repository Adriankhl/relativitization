package relativitization.universe.mechanisms.defaults.regular.storage

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object BalanceFuel : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val target: MutableFuelRestMassTargetProportionData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassTargetProportionData
        val fuelData: MutableFuelRestMassData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassData

        val totalWeight: Double = target.total()

        val targetStorage: Double = if (totalWeight > 0.0) {
            target.storage / totalWeight * fuelData.total()
        } else {
            0.25 * fuelData.total()
        }

        if (fuelData.storage > targetStorage) {
            val redistributeAmount: Double = fuelData.storage - targetStorage
            fuelData.storage = targetStorage
            mutablePlayerData.playerInternalData.physicsData().addInternalFuel(redistributeAmount)
        }

        return listOf()
    }
}