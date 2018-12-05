package production.toth.attila.homesecurityinkotlin

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.util.Log
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.BlockingQueue

class ImageConsumer(
        q: BlockingQueue<Bitmap>,
        fragment: Fragment
): Runnable {

    val imageConsumerTAG = "ImageConsumerTAG"
    private var firstBitmap: Bitmap? = null
    private var secondBitmap: Bitmap? = null
    private var startTime: Long = 0
    private var difference: Long = 0
    private var a: Fragment = fragment
    private var queue: BlockingQueue<Bitmap> = q

    /*private object Holder { val INSTANCE = ImageConsumer() }

    companion object {
        val instance: ImageConsumer by lazy { Holder.INSTANCE }
    }*/

    var callback: INotificationCallback = fragment as INotificationCallback

    override fun run() {
        try {
            secondBitmap = queue.take()
            while (true) {
                firstBitmap = queue.take()
                startTime = System.currentTimeMillis()
                val percent = getDifferenceInPercent(firstBitmap, secondBitmap)
                if (percent > 3) {
                    firstBitmap?.let { fb -> val uploadFile = persistImage( fb, "RobberFound")
                        val switchValues = a.activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
                        RetrofitNetworkService(a.context).uploadImage(uploadFile, switchValues.getBoolean("emailSwitch", false))
                        //callback.sendSmsNotification()
                    }
                }
                Log.i(imageConsumerTAG, "This is the difference between two bitmaps in percentage: $percent")
                difference = System.currentTimeMillis() - startTime
                Log.i(imageConsumerTAG, "Comparision was happened, elapsed time was: " + difference + "ms")
                secondBitmap = firstBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDifferenceInPercent(bmp1: Bitmap?, bmp2: Bitmap?): Double {
        if (bmp1 != null && bmp2 != null && bmp1.width == bmp2.width && bmp1.height == bmp2.height) {
            val height = bmp1.height
            val width = bmp1.width
            var diff: Long = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    diff += pixelDiff(bmp1.getPixel(x, y), bmp2.getPixel(x, y)).toLong()
                }
            }
            val maxDiff = 3L * 255 * width.toLong() * height.toLong()

            return 100.0 * diff / maxDiff
        }
        return 0.0
    }

    private fun pixelDiff(rgb1: Int, rgb2: Int): Int {
        val r1 = rgb1 shr 16 and 0xff
        val g1 = rgb1 shr 8 and 0xff
        val b1 = rgb1 and 0xff
        val r2 = rgb2 shr 16 and 0xff
        val g2 = rgb2 shr 8 and 0xff
        val b2 = rgb2 and 0xff
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2)
    }

    private fun persistImage(bitmap: Bitmap, name: String): File {
        val filesDir = a.activity.baseContext.filesDir
        val imageFile = File(filesDir, "$name.jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile
    }
}