package relativitization.server.routes

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import relativitization.server.UniverseServerInternal

fun Route.universeStatueRouting(universeServerInternal: UniverseServerInternal) {
    // General status
    route("/status") {
        get {
            call.respond(
                status = HttpStatusCode.OK,
                universeServerInternal.getUniverseStatusMessage()
            )
        }
    }

    // Get all available ids
    route("/status/ids") {
        get {
            call.respond(status = HttpStatusCode.OK, universeServerInternal.getAvailableIdList())
        }
    }

    // Get all available ids
    route("/status/human-ids") {
        get {
            call.respond(
                status = HttpStatusCode.OK,
                universeServerInternal.getAvailableHumanIdList()
            )
        }
    }

    // For testing
    route("/status/hello") {
        get {
            call.respondText("Hello, world!")
        }
    }

}

fun Application.registerUniverseStatusRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        universeStatueRouting(universeServerInternal)
    }
}
