package nl.jochem.packetserver.manager

import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packets.ServerClosePacket
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacketType
import java.io.OutputStream
import java.lang.Exception
import java.net.ConnectException
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class PacketClient(private val address: String, private val port: Int, private val serverID: UUID) : PacketControl() {
    private var connection: Socket? = null
    private var reader: Scanner? = null
    private var writer: OutputStream? = null

    init {
        enable()
        read()
    }

    private fun enable() {
        GlobalScope.launch {
            try {
                if(connection == null) {
                    connection = Socket(address, port)
                    reader = Scanner(connection!!.getInputStream())
                    writer = connection!!.getOutputStream()

                    read()
                    println("Connected to master server at $address on port $port [client]")
                }
            } catch (ex: ConnectException) {
                connection 
                Thread.sleep(1000)
                enable()
            }
        }
    }

    override fun send(packet: Packet, serverID: UUID?, exclude: UUID?) {
        if(connection == null) {
            if(packet.packetID != ServerOpenPacket.ID) loggedPackets.add(packet)
            return println("Socket server is offline. Couldn't send the packet ${packet.packetID}")
        }
        if(logged) println("Send packet: ${packet.packetID} (${packet::class.java.createName()})")

        try {
            writer?.write((GsonBuilder().create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
        } catch (ex: Exception) {
            loggedPackets.add(packet)
            disableServer()
        }
    }

    private fun read() {
        GlobalScope.launch(Dispatchers.IO) {
            send(ServerOpenPacket(serverID))
            loggedPackets.forEach {
                send(it)
            }
            while (connection != null) {
                try {
                    if(reader!!.hasNextLine()) {
                        val text = reader!!.nextLine()

                        val packet = getPacketType(text)
                        if(packet != null) {
                            if(packet.packetID == ServerClosePacket::class.java.createName()) {
                                disable()
                            }
                            recieve(text, packet)
                        }else{
                            println("PacketClient.getPacketType(text) == null")
                            println("========================================")
                            println(text)
                            println("========================================")
                        }
                    }
                } catch (ex: Exception) {
                    disableServer()
                }
            }
            cancel()
        }
    }

    private fun disableServer(hard: Boolean = false) {
        connection?.close()
        reader?.close()
        writer?.close()

        connection = null
        reader = null
        writer = null

        if(!hard) enable()

        println("Detected that the packet server is offline. Waiting to come back online!")
    }

    override fun disable() {
        disableServer(true)
        println("Connection $port closed")
        PacketManager.shutdown = true
    }
}