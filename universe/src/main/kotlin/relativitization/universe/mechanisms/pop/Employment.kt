package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.physics.FuelRestMassData
import relativitization.universe.data.components.physics.MutableFuelRestMassData
import relativitization.universe.data.components.popsystem.MutableCarrierData
import relativitization.universe.data.components.popsystem.pop.PopType
import relativitization.universe.data.components.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

object Employment : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )


        val fuelRestMassData: MutableFuelRestMassData =
            mutablePlayerData.playerInternalData.physicsData().fuelRestMassData

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach {

        }

        return listOf()
    }

    fun updateEmployment(
        gamma: Double,
        carrierData: MutableCarrierData,
        fuelRestMassData: MutableFuelRestMassData,
    ) {
        updateLabourerEmployment(
            gamma,
            carrierData.allPopData.labourerPopData,
            fuelRestMassData,
        )
    }

    fun updateLabourerEmployment(
        gamma: Double,
        labourerPopData: MutableLabourerPopData,
        fuelRestMassData: MutableFuelRestMassData,
    ) {
        labourerPopData.commonPopData.salary
    }
}