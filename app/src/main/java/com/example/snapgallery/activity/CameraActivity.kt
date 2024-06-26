package com.example.snapgallery.activity

import com.example.snapgallery.R
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.SensorEventListener
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.snapgallery.databinding.ActivityCameraBinding
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager

typealias LumaListener = (luma: Double) -> Unit

class CameraActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val shakeThreshold = 3f
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var lastShakeTime: Long = 0
    private lateinit var viewBinding: ActivityCameraBinding


    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }

    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    private var currentMode = "Photo"
    private var flashLightOn = false
    private var recordingPaused = false
    private var recordingMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.backButton.setOnClickListener { finish() }
        viewBinding.muteButton.setOnClickListener { muteVideo() }
        viewBinding.pauseButton.setOnClickListener { pauseVideo() }
        viewBinding.switchFlashlightButton.setOnClickListener { switchFlashlight() }
        viewBinding.photoMode.setOnClickListener { switchMode("Photo") }
        viewBinding.videoMode.setOnClickListener { switchMode("Video") }
        viewBinding.switchLensButton.setOnClickListener { switchLens() }
        viewBinding.recordButton.setOnClickListener {
            if (currentMode == "Photo") takePhoto()
            else captureVideo()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }
    override fun onResume() {
        super.onResume()
        // Rejestracja listenera sensora akcelerometru
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Wyrejestrowanie listenera sensora akcelerometru
        sensorManager.unregisterListener(this)
    }

    private var shakeCount = 0

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > 1000) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val deltaX = x - lastX
                val deltaY = y - lastY
                val deltaZ = z - lastZ

                val speed = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / SensorManager.GRAVITY_EARTH
                if (speed > shakeThreshold) {
                    lastShakeTime = currentTime
                    shakeCount++

                    if (shakeCount >= 2) {
                        // Obsługa dwukrotnego potrząśnięcia: włącz latarkę
                        switchFlashlight()
                        shakeCount = 0 // Zresetuj licznik po dwukrotnym potrząśnięciu
                    }
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nie jest wymagane
    }
    private fun muteVideo() {
        recordingMuted = !recordingMuted

        val icon =
            if (recordingMuted) R.drawable.volume_off
            else R.drawable.volume_on

        viewBinding.muteButton.setBackgroundResource(icon)

        recording?.mute(recordingMuted)
    }

    private fun pauseVideo() {
        val curRecording = recording

        if (curRecording != null) {
            if (recordingPaused) {
                curRecording.resume()
                viewBinding.pauseButton.setBackgroundResource(R.drawable.pause)
            } else {
                curRecording.pause()
                viewBinding.pauseButton.setBackgroundResource(R.drawable.play)
            }

            recordingPaused = !recordingPaused
        }
    }

    private fun switchFlashlight() {
        flashLightOn = !flashLightOn

        val icon =
            if (flashLightOn) R.drawable.baseline_flash_on_24
            else R.drawable.baseline_flash_off_24

        viewBinding.switchFlashlightButton.setBackgroundResource(icon)

        startCamera()
    }

    private fun switchMode(type: String) {
        if (currentMode == type) return

        if (type == "Video") {
            currentMode = "Video"
            viewBinding.videoMode.setTextColor(Color.parseColor("#ff0000"))
            viewBinding.photoMode.setTextColor(Color.parseColor("#ffffff"))
            viewBinding.recordButton.text = getString(R.string.start_capture)
            viewBinding.muteButton.visibility = View.VISIBLE
        } else {
            currentMode = "Photo"
            viewBinding.videoMode.setTextColor(Color.parseColor("#ffffff"))
            viewBinding.photoMode.setTextColor(Color.parseColor("#ff0000"))
            viewBinding.recordButton.text = getString(R.string.take_photo)
            viewBinding.muteButton.visibility = View.GONE
        }

        startCamera()
    }

    private fun switchLens() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA)
            lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
        else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA)
            lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA

        startCamera()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    // Implements VideoCapture use case, including start and stop capturing.
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        viewBinding.recordButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            viewBinding.pauseButton.visibility = View.GONE
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        viewBinding.recordButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                            viewBinding.pauseButton.visibility = View.VISIBLE
                            recording?.mute(recordingMuted)
                        }
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(
                                TAG, "Video capture ends with error: " +
                                        "${recordEvent.error}"
                            )
                        }
                        viewBinding.recordButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = this.lensFacing

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                if (currentMode == "Photo") {
                    val camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer
                    )

                    if (camera.cameraInfo.hasFlashUnit() && flashLightOn)
                        camera.cameraControl.enableTorch(true)
                } else {
                    val camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, videoCapture
                    )

                    if (camera.cameraInfo.hasFlashUnit() && flashLightOn)
                        camera.cameraControl.enableTorch(true)
                }


            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }
}