package com.vajra.musiconmovement

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import javaosc.OSCMessage
import javaosc.OSCPortOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var cameraPreview: PreviewView? = null
    private var poseDetector: PoseDetector? = null
    private var gestureOverlay: GestureOverlayView? = null
    private var oscPortOut: OSCPortOut? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraPreview = findViewById(R.id.cameraPreview)
        gestureOverlay = findViewById(R.id.gesture_overlay)

        initCamera()

        setupOSC()

        initPoseDetector()
    }

    private fun initCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        // Set up CameraX for capturing the camera preview frames
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview use case to show camera feed on the screen
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview?.surfaceProvider)
                }

            // ImageAnalysis use case to process the camera frames
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysisUseCase ->
                    analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        @androidx.camera.core.ExperimentalGetImage
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            // Convert ImageProxy to InputImage
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            // Process the image using ML Kit Pose Detector
                            poseDetector?.process(image)
                                ?.addOnSuccessListener { pose: Pose ->
                                    // Extract pose landmarks and send OSC message
                                    val points: MutableList<PointF> = ArrayList()
                                    for (landmark in pose.allPoseLandmarks) {
                                        points.add(PointF(landmark.position.x, landmark.position.y))
                                    }

                                    // Update the overlay with detected pose points
                                    runOnUiThread { gestureOverlay?.setGesturePoints(points) }

                                    // Send OSC message for the first landmark
                                    if (points.isNotEmpty()) {
                                        val firstPoint = points[0]
                                        sendOscMessage(firstPoint.x, firstPoint.y)
                                    }

                                    // Close the ImageProxy after processing
                                    imageProxy.close()
                                }
                                ?.addOnFailureListener { e: Exception ->
                                    // Handle failure (e.g., log the error)
                                    e.printStackTrace()

                                    // Always close the ImageProxy after processing
                                    imageProxy.close()
                                }
                        } else {
                            // Close the ImageProxy if no media image is available
                            imageProxy.close()
                        }
                    }
                }

            // Select the back camera as the default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Unbind previous use cases before rebinding
            try {
                cameraProvider.unbindAll()

                // Bind use cases to the lifecycle (Preview + ImageAnalysis)
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initPoseDetector(){
        // Initialize Pose Detector
        val options: PoseDetectorOptions = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)
    }

    private fun setupOSC() {
        try {
            val remoteIP = InetAddress.getByName("192.168.0.100")//Provide the Server-Ip
            oscPortOut = OSCPortOut(remoteIP, 8000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendOscMessage(x: Float, y: Float) {
        coroutineScope.launch {
            try {
                val args: MutableList<Any?> = ArrayList()
                args.add(x)
                args.add(y)
                val argsArray: Array<Any?> = args.toTypedArray()
                val message = OSCMessage("/gesture/coordinates", argsArray)
                if(message.arguments.size == 2) {
                    Log.e("aaa", "X : ${message.arguments[0]}")
                    Log.e("aaa", "Y : ${message.arguments[1]}")
                }
                oscPortOut?.send(message)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                // Handle permission denial
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                    Utility.setCameraPermissionDeniedOnce(this)
                    Utility.showPermissionDeniedDialog(
                        this,
                        Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_CODE
                    )
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        ) && Utility.getCameraPermissionDeniedOnce(this)
                    ) {
                        Utility.showMandatoryPermissionsNeedDialog(this)
                    }
                }
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }
}