package relativitization.universe.ai.defaults.node.other.construction

import relativitization.universe.ai.defaults.consideration.building.SufficientSelfFuelFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.building.SufficientSelfResourceFactoryAtCarrierConsideration
import relativitization.universe.ai.defaults.consideration.general.BooleanConsideration
import relativitization.universe.ai.defaults.consideration.population.ForeignLabourerLessSalaryConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.BuildForeignFuelFactoryCommand
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.components.politicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.random.Rand
import kotlin.math.pow

class ForeignFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        // Only build foreign if there are sufficient self factories
        val isSelfFuelFactorySufficient: Boolean = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.keys.all {
                SufficientSelfFuelFactoryAtCarrierConsideration.isTrue(
                    planDataAtPlayer,
                    it
                )
            }

        val isSelfResourceFactorySufficient: Boolean = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.popSystemData().carrierDataMap
            .keys.all { carrierId ->
                ResourceType.factoryResourceList.all { resourceType ->
                    SufficientSelfResourceFactoryAtCarrierConsideration.isTrue(
                        planDataAtPlayer,
                        carrierId,
                        resourceType
                    )
                }
            }

        return if (isSelfFuelFactorySufficient && isSelfResourceFactorySufficient) {
            listOf(
                NewForeignFuelFactoryReasoner()
            )
        } else {
            listOf()
        }
    }
}

class NewForeignFuelFactoryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val neighborList: List<PlayerData> = planDataAtPlayer.universeData3DAtPlayer.getNeighbour(
            1
        ).shuffled(Rand.rand())

        // Only use 0.1 of production fuel to construct foreign factory
        planState.foreignConstructionFuel = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData().fuelRestMassData.production * 0.05

        return neighborList.map { playerData ->
            NewForeignFuelFactoryAtPlayerReasoner(playerData.playerId)
        }
    }
}

class NewForeignFuelFactoryAtPlayerReasoner(
    private val playerId: Int,
) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val thisPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer
            .getCurrentPlayerData()
        val otherPlayerData: PlayerData = planDataAtPlayer.universeData3DAtPlayer.get(playerId)

        // Check if factory can be built on that player
        val isTopLeader: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.playerId

        val isTopLeaderSame: Boolean = otherPlayerData.topLeaderId() == thisPlayerData.topLeaderId()
        val canBuildAsSubordinate: Boolean = isTopLeaderSame && otherPlayerData.playerInternalData
            .politicsData().allowSubordinateBuildFactory

        val canForeignInvestorBuild: Boolean = !isTopLeaderSame && otherPlayerData
            .playerInternalData.politicsData().allowForeignInvestor

        return if (isTopLeader || canBuildAsSubordinate || canForeignInvestorBuild) {

            // Compute fuel remain fraction
            val distance: Int = Intervals.intDistance(thisPlayerData.int4D, otherPlayerData.int4D)

            val fuelLossFractionPerDistance: Double =
                (thisPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance + otherPlayerData.playerInternalData
                    .playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance) * 0.5

            val fuelRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
                1.0
            } else {
                (1.0 - fuelLossFractionPerDistance).pow(distance)
            }

            // Compute average labourer salary of self
            val totalSelfLabourerSalary: Double = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.popSystemData().carrierDataMap.values.sumOf {
                    val commonPopData: MutableCommonPopData = it.allPopData.getCommonPopData(
                        PopType.LABOURER
                    )
                    commonPopData.salaryPerEmployee * commonPopData.adultPopulation *
                            commonPopData.employmentRate
                }
            val totalSelfLabourer: Double = planDataAtPlayer.getCurrentMutablePlayerData()
                .playerInternalData.popSystemData().carrierDataMap.values.sumOf {
                    it.allPopData.getCommonPopData(
                        PopType.LABOURER
                    ).adultPopulation
                }

            val averageSalary: Double = if (totalSelfLabourer > 0.0) {
                totalSelfLabourerSalary / totalSelfLabourer
            } else {
                0.0
            }

            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.shuffled(
                Rand.rand()
            ).map {
                NewForeignFuelFactoryAtCarrierReasoner(
                    playerId = playerId,
                    carrierId = it,
                    fuelRemainFraction = fuelRemainFraction,
                    averageSelfLabourerSalary = averageSalary
                )
            }
        } else {
            listOf()
        }
    }
}

/**
 * Consider building a fuel factory at a foreign carrier
 *
 * @property fuelRemainFraction the estimated remain fraction of fuel after logistic loss
 * @property averageSelfLabourerSalary average salary of labourer
 */
class NewForeignFuelFactoryAtCarrierReasoner(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelRemainFraction: Double,
    private val averageSelfLabourerSalary: Double,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        BuildForeignFuelFactoryOption(
            playerId = playerId,
            carrierId = carrierId,
            fuelRemainFraction = fuelRemainFraction,
            averageSelfLabourerSalary = averageSelfLabourerSalary
        ),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
    )
}

/**
 * Option to build a foreign fuel factory at a carrier
 *
 * @property fuelRemainFraction the estimated remain fraction of fuel after logistic loss
 * @property averageSelfLabourerSalary average salary of labourer
 */
class BuildForeignFuelFactoryOption(
    private val playerId: Int,
    private val carrierId: Int,
    private val fuelRemainFraction: Double,
    private val averageSelfLabourerSalary: Double,
) : DualUtilityOption() {
    // maximum fraction of fuel to be used to construct the foreign factory
    private val maxFuelFraction: Double = 0.1

    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        // Make sure all self carriers have sufficient fuel and resource factory
        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(
                1E4 / fuelRemainFraction
            )
        val sufficientFuelConsideration = BooleanConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 1.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 0.0,
            bonusIfFalse = 0.0,
        ) { _, _ ->
            planState.foreignConstructionFuel * maxFuelFraction > minFuelNeeded
        }

        // Compare the salary adjusted by logistic loss
        // Prob > 0 if salary is lower then average self salary
        val foreignLabourerLessSalaryConsideration = ForeignLabourerLessSalaryConsideration(
            otherPlayerId = playerId,
            otherPlayerCarrierId = carrierId,
            referenceSalary = averageSelfLabourerSalary * fuelRemainFraction * fuelRemainFraction,
            initialBonus = 0.1,
            exponent = 2.0,
            rank = 1,
            multiplier = 1.0
        )

        return listOf(
            sufficientFuelConsideration,
            foreignLabourerLessSalaryConsideration,
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val idealFactory: MutableFuelFactoryInternalData = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .idealFuelFactory

        val fuelNeededPerEmployee: Double = planDataAtPlayer
            .getCurrentMutablePlayerData()
            .playerInternalData
            .playerScienceData()
            .playerScienceApplicationData
            .newFuelFactoryFuelNeededByConstruction(1.0)

        val fuelAvailable: Double = planState.foreignConstructionFuel * 0.1

        // Update plan state
        planState.foreignConstructionFuel -= fuelAvailable

        val maxNumEmployee: Double = fuelAvailable * 0.5 / fuelNeededPerEmployee
        val storedFuelRestMass: Double = fuelAvailable * 0.5

        planDataAtPlayer.addCommand(
            BuildForeignFuelFactoryCommand(
                toId = playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                senderTopLeaderId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                targetCarrierId = carrierId,
                ownerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fuelFactoryInternalData = DataSerializer.copy(idealFactory),
                maxNumEmployee = maxNumEmployee,
                storedFuelRestMass = storedFuelRestMass,
                senderFuelLossFractionPerDistance = planDataAtPlayer.getCurrentMutablePlayerData()
                    .playerInternalData.playerScienceData().playerScienceApplicationData
                    .fuelLogisticsLossFractionPerDistance,
            )
        )
    }
}