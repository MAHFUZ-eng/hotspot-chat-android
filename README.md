# Hotspot Chat 📶

An offline Android group-chat application that allows nearby users to communicate through a mobile hotspot without requiring internet access or mobile data.

## Current Status

This project is an early prototype.

It currently supports:

- Android Jetpack Compose interface
- Username entry
- Host chat mode
- Join existing chat mode
- Local TCP communication over Wi-Fi/hotspot
- Basic group text messaging

## How It Works

1. One Android phone creates a mobile hotspot.
2. The host opens Hotspot Chat and selects **Host Chat**.
3. Other phones connect to the host's hotspot.
4. They open the app and select **Join Existing Chat**.
5. They enter the host phone's local IP address.
6. All connected users can exchange messages locally.

The app does not require internet access for local communication.

## Requirements

- Android Studio
- Android device running Android 8.0 or newer
- Two or more Android phones for testing
- A mobile hotspot
- All phones connected to the same hotspot

## Running the Project

1. Clone this repository.
2. Open it in Android Studio.
3. Wait for Gradle synchronization to finish.
4. Connect an Android phone or start an emulator.
5. Click **Run**.

## Testing With Two Phones

### Host phone

1. Turn on the mobile hotspot.
2. Open the app.
3. Enter a username.
4. Select **Host Chat**.
5. Note the displayed IP address.

### Joining phone

1. Connect to the host phone's hotspot.
2. Open the app.
3. Enter a different username.
4. Select **Join Existing Chat**.
5. Enter the host phone's IP address.

## Important Limitations

- The host phone must keep the app open.
- Messages are not permanently stored yet.
- The current interface is a prototype.
- QR-code joining is not implemented yet.
- Reconnection handling is still being improved.
- Image sharing and private messaging are planned features.
- The app has not yet been prepared for production release.

## Roadmap

- Improve the WhatsApp-style interface
- Add group creation and group names
- Add QR-code joining
- Add automatic reconnection
- Add persistent local message history
- Add private messaging
- Add image sharing
- Add online member list
- Add group-owner controls
- Improve crash handling and background hosting
- Create a release-ready APK

## License

License to be decided.
