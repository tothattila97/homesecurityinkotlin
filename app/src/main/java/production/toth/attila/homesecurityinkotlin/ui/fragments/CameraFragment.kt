package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import production.toth.attila.homesecurityinkotlin.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class CameraFragment: Fragment(), INotificationCallback {

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private val fragmentTAG: String = "CameraFragmentTag"

    private lateinit var imageConsumer: ImageConsumer
    private var previewPictures: BlockingQueue<Bitmap> = LinkedBlockingQueue<Bitmap>(15)
    private var timeStart: Long = 0; private var timeDifference: Long = 0
    private var isSupervisionStarted: Boolean = false

    private val RECORDER_SAMPLERATE = 8000
    private val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private var recorder: AudioRecord? = null
    private val bufferElementsToRec = 1024 // want to play 2048 (2K) since 2 bytes we use only 1024
    private val bytesPerElement = 2 // 2 bytes in 16bit format
    private lateinit var audioConsumer: AudioConsumer
    private var audiosInByteArray: BlockingQueue<ByteArray> = LinkedBlockingQueue<ByteArray>()
    private var audioConsumerThread: Thread? = null
    private var rootView: View? = null

    private val previewCallback = Camera.PreviewCallback { data, _ ->
        timeDifference = System.currentTimeMillis() - timeStart
        if (timeDifference >= 500 && isSupervisionStarted) {
            val previewPicture: ByteArray = data ?: run {
                Log.d(fragmentTAG, ("Camera preview picture read did not succeeded, the value is null"))
                return@PreviewCallback
            }

            try {
                val out = ByteArrayOutputStream()
                val width: Int = mCamera?.parameters?.previewSize?.width as Int
                val height: Int = mCamera?.parameters?.previewSize?.height as Int
                val yuvImage = YuvImage(previewPicture, ImageFormat.NV21, width, height, null)
                yuvImage.compressToJpeg(Rect(0, 0, width, height), 50, out)
                val ujpreviewPicture = out.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(ujpreviewPicture, 0, ujpreviewPicture.size)
                previewPictures.put(bitmap)
                timeStart = System.currentTimeMillis()
            } catch (e: InterruptedException) {
                Log.d(fragmentTAG, "Byte array can not put into the queue: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val list = listOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //managePermissions = ManagePermissions(activity as TestActivity,list,PermissionsRequestCode)

        imageConsumer = ImageConsumer(previewPictures, this)
        audioConsumer = AudioConsumer(audiosInByteArray, this)

        // Create an instance of Camera
        mCamera = getCameraInstance()

        val params: Camera.Parameters? = mCamera?.parameters
        val focusModes: List<String>? = params?.supportedFocusModes
        if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) == true) {
            // Autofocus mode is supported
            val parameters: Camera.Parameters? = mCamera?.parameters
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            mCamera?.parameters = parameters
        }
        params?.setPreviewSize(640,480)
        mCamera?.parameters = params
        mCamera?.setDisplayOrientation(90)
        mCamera?.setFaceDetectionListener(MyFaceDetectionListener())

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(context, it, previewCallback)
        }
        //managePermissions.checkPermissions()
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferElementsToRec * bytesPerElement)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.fragment_camera, container, false)

        // Set the Preview view as the content of our activity.
        mPreview?.also {
            val preview: FrameLayout = rootView!!.findViewById(R.id.camera_preview)
            //if(it.parent != null) { (it.parent as ViewGroup).removeView(it)}
            preview.addView(it)
        }

        val captureButton: Button = rootView!!.findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            timeStart = System.currentTimeMillis()
            isSupervisionStarted = true
            val imageConsumerThread = Thread(imageConsumer)
            imageConsumerThread.start()
            //startRecording()
        }

        return rootView
    }

    override fun onPause() {
        super.onPause()
        //releaseCamera()
        //stopRecording()
    }

    fun releaseCamera() {
        if(mCamera != null){
            mCamera?.release()
            mCamera = null
        }
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /** A safe way to get an instance of the Camera object. */
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }

    override fun playRingtone() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notification)
            ringtone.play()
            //TODO: Somewhere should to stop the ringtone
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording(){
        recorder?.startRecording()
        thread { writeBytesToQueue() }
        audioConsumerThread = Thread(audioConsumer)
        audioConsumerThread?.start()
    }

    private fun writeBytesToQueue(){
        val sData = ByteArray(bufferElementsToRec)
        recorder?.read(sData,0, bufferElementsToRec)
        audiosInByteArray.put(sData)
    }

    private fun stopRecording(){
        if(recorder != null){
            recorder?.stop()
            recorder?.release()
            recorder = null
            audioConsumerThread = null
        }
    }

    override fun sendSmsNotification() {
        val manager = SmsManager.getDefault()

        val piSend = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
        val piDelivered = PendingIntent.getBroadcast(context, 0,  Intent("SMS_DELIVERED"), 0)

        val userLogin = context.getSharedPreferences("userLogin", Context.MODE_PRIVATE)
        val phoneNumber = userLogin.getString("userLogin", "")
        val message = "Something happened in your home. Please check your uploaded images."

        manager.sendTextMessage(phoneNumber, null, message, piSend, piDelivered)
    }

    internal class MyFaceDetectionListener : Camera.FaceDetectionListener {

        override fun onFaceDetection(faces: Array<Camera.Face>, camera: Camera) {
            if (faces.isNotEmpty()) {
                Log.d("FaceDetection", ("face detected: ${faces.size}" +
                        " Face 1 Location X: ${faces[0].rect.centerX()}" +
                        "Y: ${faces[0].rect.centerY()}"))
            }
        }
    }

}