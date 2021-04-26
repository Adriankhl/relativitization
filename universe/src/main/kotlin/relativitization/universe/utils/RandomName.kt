package relativitization.universe.utils

import io.github.serpro69.kfaker.Faker
import relativitization.universe.data.MutablePlayerInternalData

object RandomName {
    val faker = Faker()
    fun randomPlayerName(playerInternalData: MutablePlayerInternalData): String {
        return faker.name.name()
    }
}