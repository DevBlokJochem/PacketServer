package nl.jochem.packetserver

import com.google.gson.GsonBuilder
import nl.jochem.packetserver.packethelpers.Packet
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class PacketClient(private val port: Int) {

    private val address = "localhost"
    private var connected: Boolean = true

    private var connection: Socket = Socket(address, port)
    private var reader: Scanner = Scanner(connection.getInputStream())
    private var writer: OutputStream = connection.getOutputStream()

    init {
        println("Connected to server at $address on port $port [client]")
    }

    fun send(packet: Packet) {
        writer.write((GsonBuilder().create()!!.toJson(packet) + '\n').toByteArray(Charset.defaultCharset()))
    }

    fun disconnect() {
        connected = false
        reader.close()
        connection.close()

        println("Connection closed")
    }
}