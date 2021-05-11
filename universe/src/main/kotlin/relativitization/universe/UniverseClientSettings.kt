package relativitization.universe

/**
 *
 */
data class UniverseClientSettings(
    private var adminPassword: String,
    private var playerId: Int = -1,
    private var password: String = "player password",
    private var serverAddress: String = "127.0.0.1",
    private var serverPort: String = "29979",
    private var zLimit:Int = 10,
) {
    fun getAdminPassword(): String = adminPassword
    fun getPlayerId(): Int = playerId
}