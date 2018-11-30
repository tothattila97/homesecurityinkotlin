package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService
import production.toth.attila.homesecurityinkotlin.ui.activities.LoginActivity

class SettingsFragment: Fragment(){

    lateinit var rootView:View
    lateinit var emailSwitch: SwitchCompat
    lateinit var smsSwitch: SwitchCompat
    lateinit var noiseSwitch: SwitchCompat
    lateinit var helpTextView: TextView
    lateinit var feedbackTextView: TextView
    lateinit var languageTextView: TextView
    lateinit var logOutTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_settings, container, false);

        emailSwitch = rootView.findViewById(R.id.email_switch)
        emailSwitch.setOnClickListener {
            var switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            var editor  = switchesValues.edit()
            editor.putBoolean("emailSwitch", emailSwitch.isChecked)
            editor.apply()
        }
        smsSwitch = rootView.findViewById(R.id.sms_switch)
        smsSwitch.setOnClickListener {
            var switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            var editor  = switchesValues.edit()
            editor.putBoolean("smsSwitch", smsSwitch.isChecked)
            editor.apply()
        }
        noiseSwitch = rootView.findViewById(R.id.noise_switch)
        noiseSwitch.setOnClickListener {
            var switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            var editor  = switchesValues.edit()
            editor.putBoolean("noiseSwitch", noiseSwitch.isChecked)
            editor.apply()
        }
        languageTextView = rootView.findViewById(R.id.languageSetting_textView)
        feedbackTextView = rootView.findViewById(R.id.feedback_textView)
        helpTextView = rootView.findViewById(R.id.help_textView)
        logOutTextView = rootView.findViewById(R.id.log_out_textView)

        logOutTextView.setOnClickListener {
            var result = RetrofitNetworkService().logout()
            if(result){
                val userLogin =  activity.getSharedPreferences("userLogin", Context.MODE_PRIVATE)
                val editor  = userLogin.edit()
                editor.clear()
                editor.apply()
                val logOutIntent = Intent(activity, LoginActivity::class.java)
                startActivity(logOutIntent)
            }
        }

        return rootView
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}