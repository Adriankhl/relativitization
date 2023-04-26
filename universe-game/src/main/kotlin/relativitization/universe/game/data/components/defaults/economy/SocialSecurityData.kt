package relativitization.universe.game.data.components.defaults.economy

import ksergen.annotations.GenerateImmutable

@GenerateImmutable
data class MutableSocialSecurityData(
    var minimumWage: Double = 0.0,
    var unemploymentBenefit: Double = 0.0,
)