package production.toth.attila.homesecurityinkotlin

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(
        context: Context,
        private val mCamera: Camera,
        private val previewCallback: Camera.PreviewCallback
) : SurfaceView(context), SurfaceHolder.Callback {

    private val previewTAG: String = "CameraPreviewTag"

    private val mHolder: SurfaceHolder = holder.apply {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        addCallback(this@CameraPreview)
        // deprecated setting, but required on Android versions prior to 3.0
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        mCamera.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()

                startFaceDetection()
            } catch (e: IOException) {
                Log.d(previewTAG, "Error setting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.surface == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        mCamera.apply {
            try {
                setPreviewCallback(previewCallback)
                setPreviewDisplay(mHolder)
                startPreview()

                startFaceDetection()
            } catch (e: Exception) {
                Log.d(previewTAG, "Error starting camera preview: ${e.message}")
            }
        }
    }

    fun startFaceDetection() {
        // Try starting Face Detection
        val params = mCamera.parameters
        // start face detection only *after* preview has started

        params?.apply {
            if (maxNumDetectedFaces > 0) {
                // camera supports face detection, so can start it:
                mCamera.startFaceDetection()
            }
        }
    }
}