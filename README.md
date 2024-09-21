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

######################################################################################################################################################################################################################<br/>

### Prerequisites<br/>
Before you begin, make sure you have the following:<br/>

**1. Android Studio:** Installed and updated to the latest version.<br/>
**2. Git:** Installed on your system to clone the repository.<br/>
**3. A physical Android device:** Required for testing, as the app uses the camera and device sensors.<br/>
**4. USB Debugging Enabled:** On your Android device.<br/>

## Step-by-Step Guide<br/>

**Step 1: Clone the Repository**<br/>

First, open a terminal window and run the following command to clone the repository using Git:
```bash
git clone https://github.com/your-username/MusicOnMovement.git
```
**Step 2: Open the Project in Android Studio**

1. **Launch Android Studio**.
2. From the welcome screen, select **Open an Existing Project**.
3. Navigate to the directory where you cloned the project, and click on the **MusicOnMovement** folder.
4. Click **OK** to load the project.
5. Android Studio will take some time to load the project, sync dependencies, and index the code.

**Step 3: Sync Project with Gradle Files**
1. After the project is loaded, Android Studio will automatically attempt to sync the Gradle files.
2. If the sync does not start automatically, go to the **File** menu and select **Sync Project with Gradle Files**.
3. Make sure the **Gradle Build** is successful before proceeding.

If any dependencies are missing, you will see an error message. Android Studio might prompt you to install additional tools or SDK components (such as the Android SDK or CameraX libraries). Follow the prompts to install them.

**Step 4: Build the App**<br/>

Once all dependencies are synced and Gradle build is successful, you can build the app:

1. Go to the **Build** menu in Android Studio.
2. Select **Build Bundle(s) / APK(s) > Build APK(s)**.
3. Wait for the build process to complete.
Alternatively, you can click the **Run** button (green play icon) in the toolbar, which will build and run the app on a connected device.

**Step 5: Connect a Physical Android Device**<br/>

Since the MusicOnMovement app uses the camera and device sensors, it should be tested on a real Android device.

1. Connect your Android device to your computer via USB.
2. Ensure that USB Debugging is enabled on your device. You can enable **USB Debugging** by going to **Settings > Developer Options > USB Debugging**.
    - If Developer Options are not visible, go to **Settings > About Phone** and tap the **Build Number** 7 times to enable it.
4. When the device is connected, Android Studio will detect it.

**Step 6: Run the App on the Device**<br/>
1. Select the connected Android device from the device dropdown in Android Studio.
2. Click the **Run** button (green play icon) in the toolbar.
3. Android Studio will install the app on the connected device and launch it.


**Troubleshooting**
- **Gradle Sync Issues:** If you encounter problems with Gradle dependencies, try clearing the Gradle cache or invalidating Android Studio caches by going to **File > Invalidate Caches / Restart**.
- **Device Not Detected:** If your device is not recognized by Android Studio, ensure that **USB Debugging** is enabled, and your USB cable and port are functioning properly.
- **Permission Denials:** If the app crashes due to permission issues, verify that the required permissions are included in the manifest and that the user has granted them at runtime.
