package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.PacketManager
import java.net.ServerSocket
import java.util.*
import kotlin.collections.HashMap

class PacketServer(port: Int) : PacketControl() {

    private val server: ServerSocket = ServerSocket(port)
    private val clients: HashMap<UUID, ServerClient> = HashMap()
    private val instance: PacketServer = this

    init {
        println("Packet server is running on port ${server.localPort}")

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val client = server.accept()
                if(client.inetAddress.hostAddress != "127.0.0.1") client.close()
                println("Client connected: ${client.inetAddress.hostAddress}")

                // Run client in it's own thread.
                ServerClient(client, instance).run()
            }
        }
    }

    internal fun createClient(serverID: UUID, serverClient: ServerClient) {
        clients[serverID] = serverClient
    }

    fun disableClient(serverID: UUID) {
        if(clients.containsKey(serverID)) {
            clients[serverID]!!.disable()
            clients.remove(serverID)
        }
    }

    fun getClients(): Map<UUID, ServerClient> = clients.toMap()

    override fun disable() {
        clients.forEach {
            it.value.disable()
        }
        server.close()
        println("Server master closed")
        PacketManager.shutdown = true
    }
}