package relativitization.universe.data.component.economy

import kotlinx.serialization.Serializable

@Serializable
data class SocialSecurityData(
    val minimumWage: Double = 0.0,
    val unemploymentBenefit: Double = 0.0,
)

@Serializable
data class MutableSocialSecurityData(
    var minimumWage: Double = 0.0,
    var unemploymentBenefit: Double = 0.0,
)