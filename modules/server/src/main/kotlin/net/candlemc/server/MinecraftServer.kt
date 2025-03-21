package net.candlemc.server

import net.candlemc.logger.Logger
import net.candlemc.protocol.Client
import net.candlemc.protocol.PacketRegistries
import net.candlemc.protocol.Protocol769
import net.candlemc.protocol.codec.AbstractDataCodec
import net.candlemc.protocol.codec.MinecraftDataCodec769
import net.candlemc.types.resource.Identifier
import net.candlemc.types.resource.Registry
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MinecraftServer {
    private val logger: Logger = Logger()
    private val threadPool: ExecutorService = Executors.newCachedThreadPool()
    private var dataCodecRegistry: Registry<AbstractDataCodec>? = null
    private var registryRegistry: Registry<Registry<*>>? = null

    init {
        initRegistries()
    }

    private fun initRegistries() {
        dataCodecRegistry = Registry(Identifier.of("candlemc", "data_codec"))
        registryRegistry = Registry(Identifier.of("candlemc", "registry"))
        registryRegistry!!.register(dataCodecRegistry!!)
        dataCodecRegistry!!.register(MinecraftDataCodec769)
        PacketRegistries.register(Protocol769)
        registryRegistry!!.register(PacketRegistries)
    }

    fun start(port: Int) {
        logger.info("Starting MinecraftServer on port $port ...")
        try {
            ServerSocket(port).use { serverSocket ->
                logger.info("Server is running. Waiting for connections...")
                while (true) {
                    val clientSocket: Socket = serverSocket.accept()
                    val client: Client = Client(clientSocket)
                    threadPool.execute(client)
                }
            }
        } catch (e: IOException) {
            logger.fatal("Error when starting the server!")
            logger.stacktrace(e)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = MinecraftServer()
            server.start(25565)
        }
    }
}