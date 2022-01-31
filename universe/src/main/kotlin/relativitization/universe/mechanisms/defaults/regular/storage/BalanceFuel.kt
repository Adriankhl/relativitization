package relativitization.universe.mechanisms.defaults.regular.storage

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableTargetFuelRestMassProportionData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object BalanceFuel : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val target: MutableTargetFuelRestMassProportionData = mutablePlayerData.playerInternalData.physicsData()
            .targetFuelRestMassProportionData
        val fuelData: MutableFuelRestMassData = mutablePlayerData.playerInternalData.physicsData()
            .fuelRestMassData

        val totalWeight: Double = target.total()

        val targetStorage: Double = if (totalWeight > 0.0) {
            target.storage / totalWeight * fuelData.total()
        } else {
            0.25 * fuelData.total()
        }

        if (fuelData.storage > targetStorage) {
            val redistributeFuel: Double = fuelData.storage - targetStorage
            fuelData.storage = targetStorage
            mutablePlayerData.playerInternalData.physicsData().addFuel(redistributeFuel)
        }

        return listOf()
    }
}