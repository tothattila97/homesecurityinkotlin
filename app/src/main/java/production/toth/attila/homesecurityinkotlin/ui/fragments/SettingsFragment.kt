package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import production.toth.attila.homesecurityinkotlin.R

class SettingsFragment: Fragment(){

    lateinit var rootView:View
    lateinit var emailSwitch: SwitchCompat
    lateinit var smsSwitchCompat: SwitchCompat
    lateinit var noiseSwitchCompat: SwitchCompat
    lateinit var helpTextView: TextView
    lateinit var feedbackTextView: TextView
    lateinit var languageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_settings, container, false);

        emailSwitch = rootView.findViewById(R.id.email_switch)
        smsSwitchCompat = rootView.findViewById(R.id.sms_switch)
        noiseSwitchCompat = rootView.findViewById(R.id.noise_switch)
        languageTextView = rootView.findViewById(R.id.languageSetting_textView)
        feedbackTextView = rootView.findViewById(R.id.feedback_textView)
        helpTextView = rootView.findViewById(R.id.help_textView)

        return rootView
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}