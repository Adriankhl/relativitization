package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerInternal
import relativitization.universe.communication.*

fun Route.runUniverseRouting(universeServerInternal: UniverseServerInternal) {
    route("/run/update-server-settings") {
        post {
            val universeServerSettingsMessage: UniverseServerSettingsMessage = call.receive()
            if (universeServerSettingsMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                universeServerInternal.setUniverseServerSettings(universeServerSettingsMessage.universeServerSettings)
                call.respondText("Update server settings succeed", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText("Can't update server settings, please use the correct admin password", ContentType.Text.Plain, HttpStatusCode.Unauthorized)
            }
        }
    }

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

    route("/run/universe") {
        post {
            val runUniverseMessage: RunUniverseMessage = call.receive()
            if (!universeServerInternal.getUniverseStatusMessage().isUniverseRunning) {
                if (runUniverseMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                    universeServerInternal.runUniverse()
                    call.respondText("Run universe succeed", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Can't run universe, please use the correct admin password",
                        ContentType.Text.Plain,
                        HttpStatusCode.Unauthorized
                    )
                }
            } else {
                call.respondText("Universe already running", ContentType.Text.Plain, HttpStatusCode.OK)
            }
        }
    }

    route("/run/input") {
        post {
            val commandInputMessage: CommandInputMessage = call.receive()
            val successfulInput: Boolean = universeServerInternal.humanInput(commandInputMessage)
            if (successfulInput) {
                call.respondText("Command input succeed", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText("Command input fail", ContentType.Text.Plain, HttpStatusCode.NotAcceptable)
            }
        }
    }


    route("/run/view") {
        get {
            val universeData3DMessage: UniverseData3DMessage = call.receive()
            call.respond(status = HttpStatusCode.OK, universeServerInternal.getUniverseData3D(universeData3DMessage))
        }
    }
}

fun Application.registerRunUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        runUniverseRouting(universeServerInternal)
    }
}