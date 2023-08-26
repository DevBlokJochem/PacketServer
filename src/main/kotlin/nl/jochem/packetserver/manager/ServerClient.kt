package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacketType
import nl.jochem.packetserver.utils.getSpecificPacket
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ServerClient(client: Socket, packetServer: PacketServer) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    internal val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private val packetServer: PacketServer
    private val instance: ServerClient = this

    init {
        this.packetServer = packetServer
    }

    fun run() {
        running = true
        // Welcome message
        write("Connected to the server [server]")

        GlobalScope.launch(Dispatchers.IO) {
            while (running) {
                try {
                    val text = reader.nextLine()
                    if(getPacketType(text) != null) {
                        if(getPacketType(text)!!.packetID == ServerOpenPacket::class.java.createName()) {
                            packetServer.createClient((getSpecificPacket(text, ServerOpenPacket::class.java) as ServerOpenPacket).serverID, instance)
                        }else{
                            packetServer.recieve(text, getPacketType(text) as Packet)
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    disable()
                }
            }
        }


    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    fun disable() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}