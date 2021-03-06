package production.toth.attila.homesecurityinkotlin.ui.activities

import android.Manifest
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.media.RingtoneManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import production.toth.attila.homesecurityinkotlin.*
import production.toth.attila.homesecurityinkotlin.ui.fragments.AboutFragment
import production.toth.attila.homesecurityinkotlin.ui.fragments.CameraFragment
import production.toth.attila.homesecurityinkotlin.ui.fragments.ProfileFragment
import production.toth.attila.homesecurityinkotlin.ui.fragments.SettingsFragment
import java.io.ByteArrayOutputStream
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class CameraActivity : AppCompatActivity(), INotificationCallback {
    override fun sendSmsNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private val activityTAG: String = "CameraActivityTag"

    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var imageConsumer: ImageConsumer
    private var previewPictures: BlockingQueue<Bitmap>  = LinkedBlockingQueue<Bitmap>(15)
    private var timeStart: Long = 0; private var timeDifference: Long = 0
    private var isSupervisionStarted: Boolean = false
    
    private lateinit var bottomNavigationView: BottomNavigationView

    private val previewCallback = PreviewCallback { data, _ ->
        timeDifference = System.currentTimeMillis() - timeStart
        if (timeDifference >= 500 && isSupervisionStarted){
            val previewPicture: ByteArray = data ?: run {
                Log.d(activityTAG, ("Camera preview read did not succeeded, the value is null"))
                return@PreviewCallback
            }

            try {
                val out = ByteArrayOutputStream()
                val width: Int = mCamera?.parameters?.previewSize?.width as Int
                val height: Int = mCamera?.parameters?.previewSize?.height as Int
                val yuvImage = YuvImage(previewPicture,ImageFormat.NV21, width, height ,null)
                yuvImage.compressToJpeg(Rect(0,0, width,height), 50, out)
                val ujpreviewPicture = out.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(ujpreviewPicture, 0, ujpreviewPicture.size)
                previewPictures.put(bitmap)
                timeStart = System.currentTimeMillis()
            }catch (e: InterruptedException){
                Log.d(activityTAG, "Byte array can not put into the queue: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val list = listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        managePermissions = ManagePermissions(this,list,permissionsRequestCode)

        //imageConsumer = ImageConsumer(previewPictures, this)
        //audioConsumer = AudioConsumer(audiosInByteArray, this)

        // Create an instance of Camera
        mCamera = getCameraInstance()

        val params: Camera.Parameters? = mCamera?.parameters
        val focusModes: List<String>? = params?.supportedFocusModes
        if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) == true) {
            // autoFocus mode is supported
            val parameters: Camera.Parameters? = mCamera?.parameters
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            mCamera?.parameters = parameters
        }
        params?.setPreviewSize(640,480)
        mCamera?.parameters = params
        mCamera?.setFaceDetectionListener(MyFaceDetectionListener())

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(this, it, previewCallback)
        }
        // Set the Preview view as the content of our activity.
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }
        managePermissions.checkPermissions()

        val captureButton: Button = findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            timeStart = System.currentTimeMillis()
            isSupervisionStarted = true
            val imageConsumerThread = Thread(imageConsumer)
            imageConsumerThread.start()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener{item ->
            var fragment: Fragment? = null
            when(item.itemId){
                R.id.action_one -> {fragment = ProfileFragment();true}
                R.id.action_two -> {fragment = SettingsFragment();true}
                R.id.action_three -> {fragment = CameraFragment();true}
                R.id.action_four ->{fragment = AboutFragment(); true}
                else -> {true
                }
            }
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permissionsRequestCode ->{
                val isPermissionsGranted = managePermissions
                        .processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    // Do the task now
                    toast("Permissions granted.")
                }else{
                    toast("Permissions denied.")
                }
                return
            }
        }
    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun playRingtone() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(applicationContext, notification)
            ringtone.play()
            //TODO: Should to stop the ringtone
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
