**MusicOnMovement App**<br/><br/>
**Overview**<br/>
`MusicOnMovement` is an Android application that tracks body movements using the device's camera and sends the detected gestures' coordinates via Open Sound Control (OSC) to another system (like a music application). The app captures real-time body poses and gestures using CameraX and ML Kit's Pose Detection API and visualizes these movements on the screen with a gesture overlay.

**How the App Works**<br/>

**1. Camera Input & Pose Detection:**<br/>

- The app uses **CameraX** to capture a live camera feed.<br/>
- The camera frames are analyzed using **ML Kit's Pose Detection API** to detect key body landmarks (such as shoulders, elbows, knees, etc.).<br/>
- The detected pose landmarks are represented as points (`PointF`) and visualized on the screen using a custom view (`GestureOverlayView`) which draws red circles at the detected gesture points.<br/>

**2. Gesture Visualization:**<br/>

- The detected gesture points (landmarks) are drawn on the screen with a red circle using the `GestureOverlayView`. The user can see their body movements being tracked in real-time, which helps in ensuring accuracy during motion-based sound generation.

**How the Gesture Data is Sent via OSC**<br/>

**1. OSC Setup:**

- The app uses javaosc for handling Open Sound Control (OSC) communication.
- An OSC client is created using `OSCPortOut`, which sends data to a predefined IP address and port (configurable within the app).
- The IP address and port are specified in the `setupOSC` function. By default, the app sends OSC messages to the IP `192.168.0.100` on port `8000`.<br/>

**2. Sending OSC Messages:**

- For each detected gesture, the app sends OSC messages containing the x and y coordinates of the first landmark detected (typically the nose or another primary point).
- The data is packed in an **OSCMessage** object with a specific address `/gesture/coordinates` and the x and y values as arguments.
- The OSC message is then sent asynchronously to ensure that the UI remains responsive while data is transmitted.<br/>

**3. Real-time Data Flow:**

- As the user moves in front of the camera, the detected gestures are continuously tracked.
- The corresponding coordinates are sent via OSC in real-time, which can be received by an external system (such as a sound-generating software) to create or modify music based on body movements.<br/>

**Permissions**
The app requires camera access to function properly. If the camera permission is denied, the app will show a custom dialog guiding the user to enable the permission either directly or through the app's settings.

**Customizable Features**
- **IP and Port Configuration:** The OSC destination IP and port can be modified in the `setupOSC` method of the `MainActivity`.<br/>
- **Gesture Data Customization:** The app is currently configured to send only the first landmark's x and y coordinates, but it can be modified to send multiple points or other attributes (such as velocity or angle) by adjusting the `sendOscMessage` function.

**Dependency:** <br/>
- For Receiving Sent OSC Messages and Playing the sound using received frequency and amplitude Use the dependent Client App : https://github.com/AnuragKurumaddali/MusicOnMovementsReceiver.git

**References**
- **Google ML Kit Pose Detection:**
  * https://github.com/icanerdogan/Google-MLKit-Android-Apps/tree/master/Vision%20-%20Kotlin%20%20/PoseDetection
- **JavaOSC:**
  * https://github.com/hoijui/JavaOSC/
  * https://github.com/sparks/javaosc/
