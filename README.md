# PacketServer
Send packets from one server to another

# How to install?
1) Shade the library in your project
2) Initialize the PacketManager with ManagerType.Client or ManagerType.Server. Keep in mind that you can only have 1 server at 1 port.

# How to use?
**Sending packets**

Create a data class with your packet name and extend the Packet. Give your classname as parameter. Then you have to fill in the parameters from your own packet.

Run `PacketServer.send(<your packet>)` to send your packet

**Subscribing to packets**

Run `PacketServer.subscribe(<your packet>::class.java>) { <the code that will run> }` to listen to incoming packets.