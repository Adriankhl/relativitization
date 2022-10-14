package relativitization.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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
