package nl.jochem.packetserver

import nl.jochem.packetserver.config.PacketServerSettings
import nl.jochem.packetserver.config.RegisterSettingsConfig
import nl.jochem.packetserver.manager.*
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packets.ServerClosePacket
import java.util.UUID
import java.util.function.Consumer

object PacketManager {
    private lateinit var packetControl: PacketControl
    private lateinit var config: PacketServerSettings
    private var managerType: ManagerType? = null
    private lateinit var serverID: UUID
    private var connected = false
    internal var shutdown = false

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
        if(managerType == ManagerType.Client) {
            packetControl.send(packet)
        }else{
            val packetServer = packetControl as PacketServer
            if(serverID == null) {
                packetServer.getClients().forEach {
                    if(exclude != it.key) packetControl.send(packet)
                }
            }else{
                if(packetServer.getClients().containsKey(serverID)) return packetControl.send(packet, packetServer.getClients()[serverID]!!.writer)
                println("You cannot send the packet ${packet.packetID} because the target server doesn't exists.")
            }
        }
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