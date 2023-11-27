package nl.jochem.packetserver

import com.google.gson.GsonBuilder
import nl.jochem.packetserver.config.PacketServerSettings
import nl.jochem.packetserver.config.RegisterSettingsConfig
import nl.jochem.packetserver.manager.ManagerType
import nl.jochem.packetserver.manager.PacketClient
import nl.jochem.packetserver.manager.PacketControl
import nl.jochem.packetserver.manager.PacketServer
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packets.ServerClosePacket
import java.util.*
import java.util.function.Consumer

object PacketManager {
    private lateinit var packetControl: PacketControl
    private lateinit var config: PacketServerSettings
    private var managerType: ManagerType? = null
    private lateinit var serverID: UUID
    private var connected = false
    internal var shutdown = false
    // pretty printing has to be OFF!
    var gsonBuilder = GsonBuilder()

    fun main(args: Array<String>) {
        println("===========================================")
        println("=                                         =")
        println("= The jar file 'PacketServer' is useless! =")
        println("=                                         =")
        println("===========================================")
    }

    fun enable(serverType: ManagerType) {
        if(this.managerType != null) return println("The packetserver $managerType is already enabled.")
        this.managerType = serverType
        config = RegisterSettingsConfig().getInstance()
        this.serverID = config.serverID

        if(serverType == ManagerType.Server) {
            this.packetControl = PacketServer(config.serverIP, config.serverPort)
        }else if(serverType == ManagerType.Client) {
            this.packetControl = PacketClient(config.serverIP, config.serverPort, serverID)
        }

        this.connected = true
    }

    fun <T : Packet> subscribe(type: Class<T>, priority: Int = 5, callback: Consumer<T>): SubscriptionPacket<T> = packetControl.subscribe(type, callback, priority)

    fun send(packet: Packet, serverID: UUID? = null, exclude: UUID? = null) {
        if(!connected) return
        packetControl.send(packet, serverID, exclude)
    }

    fun getServerType(): ManagerType? = managerType
    fun getConfig(): PacketServerSettings = config
    fun reloadConfig() { config = RegisterSettingsConfig().getInstance() }

    fun shutdown(): Boolean {
        if(!connected) {
            println("The server wasn't even online")
            return true
        }
        send(ServerClosePacket(serverID))
        packetControl.disable()
        return this.shutdown
    }
}