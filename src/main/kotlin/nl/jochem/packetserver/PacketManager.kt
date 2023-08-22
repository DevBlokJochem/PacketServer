package nl.jochem.packetserver

import nl.jochem.packetserver.config.PacketServerSettings
import nl.jochem.packetserver.config.RegisterSettingsConfig
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packethelpers.Packet
import java.util.UUID
import java.util.function.Consumer

object PacketManager {
    private lateinit var packetServer: PacketServer
    private lateinit var config: PacketServerSettings
    private val clients: HashMap<UUID, PacketClient> = HashMap()

    fun enable() {
        config = RegisterSettingsConfig().getInstance()

        packetServer = PacketServer(config.serverPort)
        config.clients.forEach {
            createClient(it.key, it.value)
        }
    }

    fun <T : Packet> subscribe(type: Class<T>, callback: Consumer<T>): SubscriptionPacket<T>? = packetServer.subscribe(type, callback)
    fun send(serverID: UUID, packet: Packet) {
        if(isEnabled(serverID)) return clients[serverID]!!.send(packet)
        println("You cannot send the packet ${packet.packetID} because you haven't specified a server.")
    }

    fun createClient(serverID: UUID, port: Int) {
        if(!isEnabled(serverID)) clients[serverID] = PacketClient(port)
    }

    fun isEnabled(serverID: UUID): Boolean {
        return clients.containsKey(serverID)
    }

    fun disable(serverID: UUID) {
        if(isEnabled(serverID)) {
            clients[serverID]!!.disconnect()
            clients.remove(serverID)
        }
    }
}

fun main(args: Array<String>) {
    println("===========================================")
    println("=                                         =")
    println("= The jar file 'packetserver' is useless! =")
    println("=                                         =")
    println("===========================================")
}