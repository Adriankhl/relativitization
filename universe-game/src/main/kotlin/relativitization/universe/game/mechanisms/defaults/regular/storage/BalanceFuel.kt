package relativitization.universe.game.mechanisms.defaults.regular.storage

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.physics.total
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