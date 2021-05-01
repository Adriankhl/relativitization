package relativitization.server

import relativitization.universe.data.serializer.DataSerializer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.serialization.*
import org.apache.logging.log4j.LogManager
import relativitization.server.routes.registerCreateUniverseRoutes
import relativitization.server.routes.registerUniverseStatusRoutes
import relativitization.universe.Universe
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse


class UniverseServer(adminPassword: String) {
    val serverState: UniverseServerState = UniverseServerState(adminPassword)

    val ktorServer = embeddedServer(
        CIO,
        configure = {
            connectionIdleTimeoutSeconds = 30
        },
        environment = applicationEngineEnvironment {

            module {
                install(ContentNegotiation) {
                    json(DataSerializer.format)
                }
                install(StatusPages) {
                    exception<Throwable> { cause ->
                        logger.warn("Wrong request causing exception in server:w" +
                                "")
                        call.respondText("Something wrong in the request", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    }
                }
                registerUniverseStatusRoutes(serverState)
                registerCreateUniverseRoutes(serverState)
            }

            connector {
                port = 29979
                host = "127.0.0.1"
            }

        }
    )

    suspend fun start() {
        ktorServer.start(true)
    }

    suspend fun stop() {
        ktorServer.stop(1000, 1000)
    }


    companion object {
        private val logger = LogManager.getLogger()
    }

}