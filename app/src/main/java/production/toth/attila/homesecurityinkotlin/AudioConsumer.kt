package production.toth.attila.homesecurityinkotlin

import android.app.Activity
import java.util.concurrent.BlockingQueue

class AudioConsumer(var audios: BlockingQueue<ByteArray>,activity: Activity): Runnable{


    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}