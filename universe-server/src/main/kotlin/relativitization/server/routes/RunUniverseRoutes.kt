package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerInternal
import relativitization.universe.Universe
import relativitization.universe.communication.LoadUniverseMessage
import relativitization.universe.communication.NewUniverseMessage
import relativitization.universe.communication.RegisterPlayerMessage
import relativitization.universe.communication.RunUniverseMessage
import relativitization.universe.data.UniverseData
import relativitization.universe.generate.GenerateUniverse

fun Route.runUniverseRouting(universeServerInternal: UniverseServerInternal) {
    // new Universe
    route("/run/register") {
        post {
            val registerPlayerMessage: RegisterPlayerMessage = call.receive()
            val successfulRegister: Boolean = universeServerInternal.registerPlayer(registerPlayerMessage)
            if (successfulRegister) {
                call.respondText("Registered player", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText("Can't register, try another id", ContentType.Text.Plain, HttpStatusCode.NotAcceptable)
            }
        }
    }

    route("/run/run-universe") {
        post {
            val runUniverseMessage: RunUniverseMessage = call.receive()
            if (runUniverseMessage.adminPassword == universeServerInternal.adminPassword) {
                universeServerInternal.runUniverse()
                call.respondText("Run universe success", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText("Can't run universe, please use the correct admin password", ContentType.Text.Plain, HttpStatusCode.Unauthorized)
            }
        }
    }
}

fun Application.registerRunUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        runUniverseRouting(universeServerInternal)
    }
}