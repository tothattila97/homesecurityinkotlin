package production.toth.attila.homesecurityinkotlin

import android.support.v4.app.Fragment
import java.util.concurrent.BlockingQueue

class AudioConsumer(var audios: BlockingQueue<ByteArray>,fragment: Fragment): Runnable{


    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}