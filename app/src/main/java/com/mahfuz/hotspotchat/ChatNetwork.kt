package com.mahfuz.hotspotchat

import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList

class ChatNetwork(
    private val onMessageReceived: (String) -> Unit,
    private val onStatusChanged: (String) -> Unit
) {
    companion object {
        const val PORT = 8988
    }

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var clientWriter: PrintWriter? = null

    private val connectedClients = CopyOnWriteArrayList<PrintWriter>()

    private var isHost = false

    fun startHost() {
        isHost = true

        Thread {
            try {
                serverSocket = ServerSocket(PORT)
                onStatusChanged("Hosting on port $PORT")

                while (!serverSocket!!.isClosed) {
                    val client = serverSocket!!.accept()
                    val writer = PrintWriter(client.getOutputStream(), true)

                    connectedClients.add(writer)

                    Thread {
                        try {
                            val reader = client.getInputStream().bufferedReader()

                            reader.forEachLine { line ->
                                if (line.startsWith("MSG|")) {
                                    val message = line.removePrefix("MSG|")

                                    onMessageReceived(message)

                                    connectedClients.forEach {
                                        it.println(line)
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            // Client disconnected
                        } finally {
                            connectedClients.remove(writer)
                            client.close()
                        }
                    }.start()
                }
            } catch (error: Exception) {
                onStatusChanged("Host error: ${error.message}")
            }
        }.start()
    }

    fun connectToHost(hostIp: String) {
        isHost = false

        Thread {
            try {
                clientSocket = Socket(hostIp, PORT)
                clientWriter = PrintWriter(
                    clientSocket!!.getOutputStream(),
                    true
                )

                onStatusChanged("Connected to host")

                val reader = clientSocket!!.getInputStream().bufferedReader()

                reader.forEachLine { line ->
                    if (line.startsWith("MSG|")) {
                        val message = line.removePrefix("MSG|")
                        onMessageReceived(message)
                    }
                }
            } catch (error: Exception) {
                onStatusChanged("Connection error: ${error.message}")
            }
        }.start()
    }

    fun sendMessage(message: String) {
        val cleanMessage = message
            .replace("\n", " ")
            .trim()

        if (cleanMessage.isEmpty()) return

        val line = "MSG|$cleanMessage"

        if (isHost) {
            onMessageReceived(cleanMessage)

            connectedClients.forEach {
                it.println(line)
            }
        } else {
            clientWriter?.println(line)
        }
    }
    fun localIpAddress(): String {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()

            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses

                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()

                    if (
                        address is java.net.Inet4Address &&
                        !address.isLoopbackAddress
                    ) {
                        return address.hostAddress ?: "Unknown"
                    }
                }
            }

            "Unknown"
        } catch (error: Exception) {
            "Unknown"
        }
    }
    fun stop() {
        try {
            serverSocket?.close()
            clientSocket?.close()
        } catch (_: Exception) {
        }
    }
}