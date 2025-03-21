package net.candlemc.protocol

import net.candlemc.io.StagedOutputStream
import net.candlemc.protocol.codec.MinecraftDataCodec769
import net.candlemc.protocol.handshake.client.HandshakePacket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class Client(private val socket: Socket) : Runnable {
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override fun run() {
        val clientAddress = socket.inetAddress.hostAddress
        try {
            inputStream = socket.getInputStream()
            outputStream = StagedOutputStream.withUnderlying(socket.getOutputStream())
            val handshake = HandshakePacket.deserializeHandshakePacket(inputStream!!, MinecraftDataCodec769)
            println("Handshake from=$clientAddress, protocol=${handshake.protocolVersion}, nextState=${handshake.nextState}")
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            try {
                socket.close()
            } catch (ignore: IOException) {
            }
        }
    }
}