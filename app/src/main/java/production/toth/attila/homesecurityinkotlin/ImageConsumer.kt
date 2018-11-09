package production.toth.attila.homesecurityinkotlin

import android.graphics.Bitmap
import android.media.Image
import android.support.v4.app.Fragment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.BlockingQueue

class ImageConsumer(
        private var q: BlockingQueue<Bitmap>,
        fragment: Fragment
): Runnable {

    val ImageConsumerTAG = "somethingHappened"
    private var first: Image? = null
    private var firstbitmap: Bitmap? = null
    private var second: Image? = null
    private var secondbitmap: Bitmap? = null
    var starttime: Long = 0
    var difference: Long = 0
    private var a: Fragment = fragment
    private var queue: BlockingQueue<Bitmap> = q

    /*private object Holder { val INSTANCE = ImageConsumer() }

    companion object {
        val instance: ImageConsumer by lazy { Holder.INSTANCE }
    }*/

    var callback: IRingtoneCallback = fragment as IRingtoneCallback

    /*fun ImageConsumer(q: BlockingQueue<Bitmap>, activity: Activity) {
        callback = activity as IRingtoneCallback
        this.a = activity
        this.queue = q
        //this.previewQueue = q
    }*/

    override fun run() {
        try {
            secondbitmap = queue.take()
            while (true) {
                firstbitmap = queue.take()
                starttime = System.currentTimeMillis()
                val percent = getDifferenceInPercent(firstbitmap, secondbitmap)
                if (percent > 3) {
                    //callback.playRingtone()
                    firstbitmap?.let { fb -> val uploadFile = persistImage( fb, "betoromegtalalva")
                        //val networkService = RetrofitUploadImplementation()
                        //networkService.uploadImage(uploadFile)
                    }
                    //val uploadFile = persistImage( firstbitmap, "betoromegtalalva")
                    //RetrofitUploadImplementation(uploadFile)  //TODO: már elérhető az Azure de kredit spórolás céljából ne töltse fel a képeket.
                }
                Log.i("homesecurity", "Ekkora volt a két bitmap közötti eltérés %-ban: $percent")
                difference = System.currentTimeMillis() - starttime
                Log.i("homesecurity", "Megtörtént az összehasonlítás, ennyi idő volt: " + difference + "ms")
                secondbitmap = firstbitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isDifferent(firstRed: Int, firstGreen: Int, firstBlue: Int, secondRed: Int, secondGreen: Int, secondBlue: Int): Boolean {
        val firstAverageR = (firstRed / 400).toDouble()
        val firstAverageG = (firstGreen / 400).toDouble()
        val firstAverageB = (firstBlue / 400).toDouble()
        val secondAverageR = (secondRed / 400).toDouble()
        val secondAverageG = (secondGreen / 400).toDouble()
        val secondAverageB = (secondBlue / 400).toDouble()
        return Math.abs(firstAverageR / secondAverageR - 1) > 0.30 ||
                Math.abs(firstAverageG / secondAverageG - 1) > 0.30 ||
                Math.abs(firstAverageB / secondAverageB - 1) > 0.30
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

    interface IRingtoneCallback {
        fun playRingtone()
    }
}