package relativitization.server.routes

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import relativitization.server.UniverseServerInternal
import relativitization.universe.communication.*

fun Route.runUniverseRouting(universeServerInternal: UniverseServerInternal) {
    route("/run/update-server-settings") {
        post {
            val universeServerSettingsMessage: UniverseServerSettingsMessage = call.receive()
            if (universeServerSettingsMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                universeServerInternal.setUniverseServerSettings(universeServerSettingsMessage.universeServerSettings)
                call.respondText(
                    "Update server settings succeed",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Can't update server settings, please use the correct admin password",
                    ContentType.Text.Plain,
                    HttpStatusCode.Unauthorized
                )
            }
        }
    }

    route("/run/register") {
        post {
            val registerPlayerMessage: RegisterPlayerMessage = call.receive()
            val isRegisterSuccess: Boolean =
                universeServerInternal.registerPlayer(registerPlayerMessage)
            if (isRegisterSuccess) {
                call.respondText("Registered player", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText(
                    "Can't register, try another id",
                    ContentType.Text.Plain,
                    HttpStatusCode.NotAcceptable
                )
            }
        }
    }

    route("/run/deregister") {
        post {
            val deregisterPlayerMessage: DeregisterPlayerMessage = call.receive()
            val isDeregisterSuccess: Boolean =
                universeServerInternal.deregisterPlayer(deregisterPlayerMessage)
            if (isDeregisterSuccess) {
                call.respondText(
                    "De-registered player",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Can't register, try another id",
                    ContentType.Text.Plain,
                    HttpStatusCode.NotAcceptable
                )
            }
        }
    }

    route("/run/universe-run") {
        post {
            val runUniverseMessage: RunUniverseMessage = call.receive()
            if (!universeServerInternal.getUniverseStatusMessage().isUniverseRunning) {
                if (runUniverseMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                    universeServerInternal.runUniverse()
                    call.respondText(
                        "Run universe succeed",
                        ContentType.Text.Plain,
                        HttpStatusCode.OK
                    )
                } else {
                    call.respondText(
                        "Can't run universe, please use the correct admin password",
                        ContentType.Text.Plain,
                        HttpStatusCode.Unauthorized
                    )
                }
            } else {
                call.respondText(
                    "Universe already running",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            }
        }
    }


    route("/run/universe-stop") {
        post {
            val runUniverseMessage: StopUniverseMessage = call.receive()
            if (universeServerInternal.getUniverseStatusMessage().isUniverseRunning) {
                if (runUniverseMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                    universeServerInternal.stopUniverse()
                    call.respondText(
                        "Stop universe succeed",
                        ContentType.Text.Plain,
                        HttpStatusCode.OK
                    )
                } else {
                    call.respondText(
                        "Can't stop universe, please use the correct admin password",
                        ContentType.Text.Plain,
                        HttpStatusCode.Unauthorized
                    )
                }
            } else {
                call.respondText(
                    "Universe already stopped",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            }
        }
    }

    route("/run/input") {
        post {
            val playerInputMessage: PlayerInputMessage = call.receive()
            val successfulInput: Boolean = universeServerInternal.humanInput(playerInputMessage)
            if (successfulInput) {
                call.respondText("Command input succeed", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText(
                    "Command input fail",
                    ContentType.Text.Plain,
                    HttpStatusCode.NotAcceptable
                )
            }
        }
    }


    route("/run/view") {
        get {
            val universeData3DMessage: UniverseData3DMessage = call.receive()
            call.respond(
                status = HttpStatusCode.OK,
                universeServerInternal.getUniverseData3D(universeData3DMessage)
            )
        }
    }


    route("/run/stop-waiting") {
        post {
            val stopWaitingMessage: StopWaitingMessage = call.receive()
            if (stopWaitingMessage.adminPassword == universeServerInternal.universeServerSettings.adminPassword) {
                universeServerInternal.isServerWaitingInput.set(false)
                call.respondText("Stop waiting success", ContentType.Text.Plain, HttpStatusCode.OK)
            } else {
                call.respondText(
                    "Stop waiting fail, please use the correct admin password",
                    ContentType.Text.Plain,
                    HttpStatusCode.Unauthorized
                )
            }
        }
    }


    route("/run/dead") {
        get {
            val checkIsPlayerDeadMessage: CheckIsPlayerDeadMessage = call.receive()
            call.respond(
                status = HttpStatusCode.OK,
                universeServerInternal.isPlayerDead(checkIsPlayerDeadMessage)
            )
        }
    }
}

fun Application.registerRunUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        runUniverseRouting(universeServerInternal)
    }
}