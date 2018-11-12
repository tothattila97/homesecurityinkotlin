package production.toth.attila.homesecurityinkotlin.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.PreviewCallback
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
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
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class CameraActivity : AppCompatActivity(), ImageConsumer.IRingtoneCallback {
    override fun sendSmsNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendEmailNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private val TAG: String = "CameraActivityTag"

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var imageConsumer: ImageConsumer
    private var previewPictures: BlockingQueue<Bitmap>  = LinkedBlockingQueue<Bitmap>(15)
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
    private lateinit var bottomNavigationView: BottomNavigationView

    private val mPicture = PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    private val previewCallback = PreviewCallback { data, _ ->
        timeDifference = System.currentTimeMillis() - timeStart
        if (timeDifference >= 500 && isSupervisionStarted){
            val previewPicture: ByteArray = data ?: run {
                Log.d(TAG, ("A camera preview kép kiolvasása nem sikerült, az értéke null"))
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

                //val bitmap: Bitmap = BitmapFactory.decodeByteArray(previewPicture , 0, previewPicture.size, null)
                previewPictures.put(bitmap)
                timeStart = System.currentTimeMillis()
            }catch (e: InterruptedException){
                Log.d(TAG, "A bájttömb belehelyezése nem sikerült a queueba:  ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val list = listOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

        //imageConsumer = ImageConsumer(previewPictures, this)
        //audioConsumer = AudioConsumer(audiosInByteArray, this)

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

    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "HomeSecurityKotlin"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode ->{
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
            //TODO: Valahol leállítani is a ringtonet stop()-al
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording(){
        recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferElementsToRec * bytesPerElement)

        recorder?.startRecording()
        audioConsumerThread = Thread(audioConsumer)
        audioConsumerThread?.start()
    }

    private fun stopRecording(){
        if(recorder != null){
            recorder?.stop();
            recorder?.release();
            recorder = null;
            audioConsumerThread = null;
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
