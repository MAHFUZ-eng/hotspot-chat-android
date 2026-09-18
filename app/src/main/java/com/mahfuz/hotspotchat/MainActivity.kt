package com.mahfuz.hotspotchat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahfuz.hotspotchat.ui.theme.HotspotChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HotspotChatTheme {
                HotspotChatApp()
            }
        }
    }
}

@Composable
fun HotspotChatApp() {
    var username by remember { mutableStateOf("") }
    var page by remember { mutableStateOf("join") }
    var connectionStatus by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf("Welcome to Hotspot Chat!")
    }

    val mainHandler = remember {
        Handler(Looper.getMainLooper())
    }

    val network = remember {
        ChatNetwork(
            onMessageReceived = { message ->
                mainHandler.post {
                    messages.add(message)
                }
            },
            onStatusChanged = { status ->
                mainHandler.post {
                    connectionStatus = status
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            network.stop()
        }
    }

    when (page) {
        "join" -> {
            JoinScreen(
                username = username,
                onUsernameChange = { username = it },
                onJoin = {
                    if (username.isNotBlank()) {
                        page = "connection"
                    }
                }
            )
        }

        "connection" -> {
            ConnectionScreen(
                username = username,
                network = network,
                onConnected = { status ->
                    connectionStatus = status
                    page = "chat"
                }
            )
        }

        "chat" -> {
            ChatScreen(
                username = username,
                status = connectionStatus,
                messages = messages,
                onSendMessage = { message ->
                    network.sendMessage("$username: $message")
                }
            )
        }
    }
}

@Composable
fun JoinScreen(
    username: String,
    onUsernameChange: (String) -> Unit,
    onJoin: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D1B1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📶 Hotspot Chat",
                color = Color(0xFF25D366),
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Your name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun ConnectionScreen(
    username: String,
    network: ChatNetwork,
    onConnected: (String) -> Unit
) {
    var hostIp by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D1B1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello, $username",
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    network.startHost()

                    val ip = network.localIpAddress()

                    onConnected("Hosting at $ip:${ChatNetwork.PORT}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Host Chat")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = hostIp,
                onValueChange = { hostIp = it },
                label = { Text("Host IP address") },
                placeholder = { Text("Example: 192.168.43.1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    if (hostIp.isNotBlank()) {
                        network.connectToHost(hostIp.trim())
                        onConnected("Connecting to $hostIp...")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join Existing Chat")
            }
        }
    }
}

@Composable
fun ChatScreen(
    username: String,
    status: String,
    messages: List<String>,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D1B1E)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Group Chat",
                color = Color(0xFF25D366),
                fontSize = 22.sp,
                modifier = Modifier.padding(20.dp)
            )

            Text(
                text = status,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(messages) { message ->
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Message") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}