package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packets.ServerClosePacket
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacketType
import nl.jochem.packetserver.utils.getSpecificPacket
import java.io.OutputStream
import java.net.Socket
import java.util.*

class ServerClient(private val client: Socket, packetServer: PacketServer) {
    private val reader: Scanner = Scanner(client.getInputStream())
    internal val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private val packetServer: PacketServer
    private val instance: ServerClient = this
    private lateinit var cliendID: UUID

    init {
        this.packetServer = packetServer
    }

    fun run() {
        running = true

        GlobalScope.launch(Dispatchers.IO) {
            while (running) {
                try {
                    if(reader.hasNextLine()) {
                        val text = reader.nextLine()

                        val packet = getPacketType(text)
                        if(packet != null) {
                            if(packet.packetID == ServerOpenPacket::class.java.createName()) {
                                val specificPacket = getSpecificPacket(text, ServerOpenPacket::class.java) as ServerOpenPacket
                                packetServer.createClient(specificPacket.serverID, instance)
                                cliendID = specificPacket.serverID
                            }else if(packet.packetID == ServerClosePacket::class.java.createName()) {
                                disable()
                                packetServer.disableClient(cliendID)
                            }

                            packetServer.recieve(text, getPacketType(text) as Packet)
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    fun disable() {
        running = false
        client.close()
        packetServer.disableClient(this)
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}