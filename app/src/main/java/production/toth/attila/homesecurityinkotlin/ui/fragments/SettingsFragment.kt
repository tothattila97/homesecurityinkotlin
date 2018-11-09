package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import production.toth.attila.homesecurityinkotlin.R

class SettingsFragment: Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_settings, container, false);
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}