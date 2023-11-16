package nl.jochem.packetserver.manager

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.utils.createName
import java.io.OutputStream
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap

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

    override fun send(packet: Packet, writer: OutputStream) {
        if(logged) println("Send packet: ${packet.packetID} (${packet::class.java.createName()})")
        writer.write((GsonBuilder().create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
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