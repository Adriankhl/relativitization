package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min
import relativitization.universe.maths.random.Rand

/**
 * Population migrate from higher salary place to lower salary place
 */
object Migration : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // determine the rate of migration
        val migrationRate: Double = 0.01

        PopType.values().forEach { popType ->
            val commonPopMap: MutableMap<Int, MutableCommonPopData> = mutablePlayerData
                .playerInternalData.popSystemData().carrierDataMap.mapValues { (_, carrier) ->
                    carrier.allPopData.getCommonPopData(popType)
                }.toMutableMap()

            for (i in (0..commonPopMap.size)) {
                if (commonPopMap.size >= 2 ) {
                    val emigrateId: Int = commonPopMap.minByOrNull { (_, commonPop) ->
                        commonPop.salaryPerEmployee * (1.0 - commonPop.unemploymentRate)
                    }?.key ?: -1

                    val emigrateCommonPop: MutableCommonPopData = commonPopMap.getValue(emigrateId)

                    commonPopMap.remove(emigrateId)

                    val immigrateId: Int = commonPopMap.keys.elementAt(Rand.rand().nextInt(commonPopMap.size))

                    val immigrateCommonPop: MutableCommonPopData = commonPopMap.getValue(immigrateId)

                    localMigrate(
                        emigrateCommonPop,
                        immigrateCommonPop,
                        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                            immigrateId
                        ).carrierInternalData.idealPopulation,
                        migrationRate,
                    )
                }
            }
        }

        return listOf()
    }

    /**
     * Emigrate from one to another
     */
    fun localMigrate(
        emigrateCommonPop: MutableCommonPopData,
        immigrateCommonPop: MutableCommonPopData,
        immigrateCarrierIdealPop: Double,
        migrationRate: Double,
    ) {
        val immigrateAmount: Double = min(
            immigrateCarrierIdealPop * migrationRate,
            emigrateCommonPop.adultPopulation * migrationRate
        )

        val otherSaving: Double = if (emigrateCommonPop.adultPopulation > 0.0) {
            emigrateCommonPop.saving * immigrateAmount / emigrateCommonPop.adultPopulation
        } else {
            0.0
        }

       immigrateCommonPop.addAdultPopulation(
           otherPopulation = immigrateAmount,
           otherEducationLevel = emigrateCommonPop.educationLevel,
           otherSatisfaction = emigrateCommonPop.satisfaction,
           otherSaving = otherSaving,
       )

    }
}