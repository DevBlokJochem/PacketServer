package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packethelpers.Packet
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*

class PacketServer(serverIP: String, port: Int) : PacketControl() {

    private val server: ServerSocket = ServerSocket(port)
    private val clients: HashMap<UUID, ServerClient> = HashMap()
    private val instance: PacketServer = this

    init {
        println("Packet server is running on port ${server.localPort}")

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val client = server.accept()
                if(serverIP == "localhost" && client.inetAddress.hostAddress != "127.0.0.1") client.close()
                println("Client connected: ${client.inetAddress.hostAddress}")

                // Run client in it's own thread.
                ServerClient(client, instance).run()
            }
        }
    }

    override fun send(packet: Packet, serverID: UUID?, exclude: UUID?) {
        if(logged) println("Send packet: ${packet.packetID})")
        if(serverID == null) {
            clients.filter { client -> client.key != exclude }.forEach {
                it.value.writer.write((PacketManager.gsonBuilder.create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
            }
        }else{
            clients.filter { client -> client.key == serverID }.forEach {
                it.value.writer.write((PacketManager.gsonBuilder.create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
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

    fun disableClient(serverClient: ServerClient)  {
        val key = clients.filter { client -> client.value == serverClient }.keys.first()
        clients.remove(key)
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