package relativitization.server.routes

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import relativitization.server.UniverseServerInternal
import relativitization.universe.core.Universe
import relativitization.universe.game.communication.LoadUniverseMessage
import relativitization.universe.game.communication.NewUniverseMessage
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.core.utils.RelativitizationLogManager

private val logger = RelativitizationLogManager.getLogger()

fun Route.createUniverseRouting(universeServerInternal: UniverseServerInternal) {
    // new Universe
    route("/create/new") {
        post {
            val newUniverseMessage: NewUniverseMessage = call.receive()
            if (newUniverseMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                logger.debug("Start generating universe")

                val universeData: UniverseData = GenerateUniverseMethodCollection.generate(
                    newUniverseMessage.generateSettings
                )

                logger.debug("Done generating universe")

                if (universeData.isUniverseValid()) {
                    universeServerInternal.setUniverse(
                        Universe(
                            universeData = universeData,
                            programDir = universeServerInternal.universeServerSettings.programDir,
                            saveWhenInit = true,
                            alwaysSaveLatest = true,
                        )
                    )
                    call.respondText("Created Universe", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Create Universe Failed, wrong setting",
                        ContentType.Text.Plain,
                        HttpStatusCode.NotAcceptable
                    )
                }
            } else {
                call.respondText(
                    "Create Universe Failed, wrong admin password",
                    ContentType.Text.Plain,
                    HttpStatusCode.Unauthorized
                )
            }
        }
    }

    // List saved universe
    route("/create/list-saved") {
        get {
            call.respond(status = HttpStatusCode.OK, universeServerInternal.getSavedUniverse())
        }
    }

    // load universe from save
    route("/create/load") {
        post {
            val loadUniverseMessage: LoadUniverseMessage = call.receive()
            if (loadUniverseMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                val universeData: UniverseData = Universe.loadUniverseLatest(
                    universeName = loadUniverseMessage.universeName,
                    programDir = universeServerInternal.universeServerSettings.programDir,
                    shouldRandomizeSeed = true,
                )
                if (universeData.isUniverseValid()) {
                    universeServerInternal.setUniverse(
                        Universe(
                            universeData = universeData,
                            programDir = universeServerInternal.universeServerSettings.programDir,
                            saveWhenInit = false,
                            alwaysSaveLatest = true,
                        )
                    )
                    call.respondText("Loaded Universe", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Load Universe Failed, wrong setting",
                        ContentType.Text.Plain,
                        HttpStatusCode.NotAcceptable
                    )
                }
            } else {
                call.respondText(
                    "Load Universe Failed, wrong admin password",
                    ContentType.Text.Plain,
                    HttpStatusCode.Unauthorized
                )
            }
        }
    }
}

fun Application.registerCreateUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        createUniverseRouting(universeServerInternal)
    }
}