package net.candlemc.protocol

class ConnectionState(private val name: String) {
    override fun toString(): String {
        return name
    }

    companion object {
        @JvmStatic
        val HANDSHAKING = ConnectionState("Handshaking")
        @JvmStatic
        val STATUS = ConnectionState("Status")
        @JvmStatic
        val LOGIN = ConnectionState("Login")
        @JvmStatic
        val CONFIGURATION = ConnectionState("Configuration")
        @JvmStatic
        val PLAY = ConnectionState("Play")
        @JvmStatic
        val TRANSFER = ConnectionState("Transfer")
    }
}
