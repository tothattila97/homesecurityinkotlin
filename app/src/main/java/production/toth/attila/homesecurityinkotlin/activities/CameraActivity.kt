package production.toth.attila.homesecurityinkotlin.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.PreviewCallback
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import production.toth.attila.homesecurityinkotlin.CameraPreview
import production.toth.attila.homesecurityinkotlin.ImageConsumer
import production.toth.attila.homesecurityinkotlin.ManagePermissions
import production.toth.attila.homesecurityinkotlin.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


class CameraActivity : AppCompatActivity() {

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private val TAG: String = "CameraActivityTag"

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var imageConsumer: ImageConsumer
    private var previewPictures: BlockingQueue<Bitmap>  = LinkedBlockingQueue<Bitmap>()
    private var timeStart: Long = 0; private var timeDifference: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val list = listOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

        imageConsumer = ImageConsumer(previewPictures, this)

        // Create an instance of Camera
        mCamera = getCameraInstance()

        val params: Camera.Parameters? = mCamera?.parameters
        val focusModes: List<String>? = params?.supportedFocusModes
        if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) == true) {
            // Autofocus mode is supported
            val params: Camera.Parameters? = mCamera?.parameters
            params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            mCamera?.parameters = params
        }
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

        val captureButton: Button = findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            // get an image from the camera
            timeStart = System.currentTimeMillis()
            mCamera?.takePicture(null, null, mPicture)
        }
        //mCamera?.setPreviewCallback(previewCallback)
        managePermissions.checkPermissions()
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /** A safe way to get an instance of the Camera object. */
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }
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
        if (timeDifference >= 500){
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

    //val MEDIA_TYPE_IMAGE = 1
    //val MEDIA_TYPE_VIDEO = 2

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
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

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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