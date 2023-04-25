package relativitization.universe.game.data.serializer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test


internal class DataSerializerTest {
    val module = SerializersModule {
        polymorphic(GrandProject::class) {
            subclass(OwnedProject::class)
        }
    }

    val format = Json { serializersModule = module }

    @Serializable
    abstract class GrandProject

    @Serializable
    sealed class Project : GrandProject() {
        abstract val name: String
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(override val name: String, val owner: String) : Project()


    @Test
    fun sealSubclassTest() {

        val data1: Project = OwnedProject("kotlinx.coroutines", "kotlin")
        val s1 = format.encodeToString(data1)


        val data2: GrandProject = OwnedProject("kotlinx.coroutines", "kotlin")
        val s2 = format.encodeToString(data2)
        assert(s1 == s2)
    }
}