package production.toth.attila.homesecurityinkotlin

import android.support.v4.app.Fragment
import android.util.Log
import java.util.concurrent.BlockingQueue

class AudioConsumer(var audios: BlockingQueue<ByteArray>,fragment: Fragment): Runnable{

    private var firstAudioArray: ByteArray? =null
    private var secondAudioArray: ByteArray? =null
    var starttime: Long = 0
    var difference: Long = 0

    override fun run() {
        try {
            secondAudioArray = audios.take()
            while (true){
                firstAudioArray = audios.take()
                starttime = System.currentTimeMillis()
                val percent = getDifferenceInPercent(firstAudioArray, secondAudioArray)
                if(percent > 5){

                }
                Log.i("homesecurityAudios", "Ekkora volt a két audiotömb közötti eltérés %-ban: $percent")
                difference = System.currentTimeMillis() - starttime
                Log.i("homesecurityAudios", "Megtörtént az összehasonlítás, ennyi idő volt: " + difference + "ms")
                secondAudioArray = firstAudioArray
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun getDifferenceInPercent(first: ByteArray?, second: ByteArray?): Double{
        if(first != null && second != null && first.size == second.size){
            var diff = 0
            for (i : Int in 0 until first.size){
                diff += Math.abs(first[i] - second[i])
            }
            val maxDiff = 255 * first.size
            return 100.0 * diff / maxDiff
        }
        return 0.0
    }
}