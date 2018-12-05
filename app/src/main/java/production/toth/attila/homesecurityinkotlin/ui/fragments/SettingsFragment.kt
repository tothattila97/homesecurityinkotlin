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
import android.widget.Toast
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.models.UserProfileModel
import production.toth.attila.homesecurityinkotlin.network.IHttpCallback
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService
import production.toth.attila.homesecurityinkotlin.ui.activities.LoginActivity

class SettingsFragment: Fragment(){

    private lateinit var rootView:View
    private lateinit var emailSwitch: SwitchCompat
    private lateinit var smsSwitch: SwitchCompat
    private lateinit var noiseSwitch: SwitchCompat
    private lateinit var helpTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var languageTextView: TextView
    private lateinit var logOutTextView: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_settings, container, false)

        emailSwitch = rootView.findViewById(R.id.email_switch)
        emailSwitch.setOnClickListener {
            val switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            val editor  = switchesValues.edit()
            editor.putBoolean("emailSwitch", emailSwitch.isChecked)
            editor.apply()
        }
        smsSwitch = rootView.findViewById(R.id.sms_switch)
        smsSwitch.setOnClickListener {
            val switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            val editor  = switchesValues.edit()
            editor.putBoolean("smsSwitch", smsSwitch.isChecked)
            editor.apply()
        }
        noiseSwitch = rootView.findViewById(R.id.noise_switch)
        noiseSwitch.setOnClickListener {
            val switchesValues =  activity.getSharedPreferences("switchesValues", Context.MODE_PRIVATE)
            val editor  = switchesValues.edit()
            editor.putBoolean("noiseSwitch", noiseSwitch.isChecked)
            editor.apply()
        }
        languageTextView = rootView.findViewById(R.id.languageSetting_textView)
        feedbackTextView = rootView.findViewById(R.id.feedback_textView)
        helpTextView = rootView.findViewById(R.id.help_textView)
        logOutTextView = rootView.findViewById(R.id.log_out_textView)

        logOutTextView.setOnClickListener {
            RetrofitNetworkService(context).logout(object : IHttpCallback {
                override fun getIsSucceeded(succeeded: Boolean) {
                    if(succeeded){
                        val userLogin =  activity.getSharedPreferences("userLogin", Context.MODE_PRIVATE)
                        val editor  = userLogin.edit()
                        editor.clear()
                        editor.apply()
                        val logOutIntent = Intent(activity, LoginActivity::class.java)
                        startActivity(logOutIntent)
                    }
                    else
                        Toast.makeText(context, "Log out failed. Try again!", Toast.LENGTH_SHORT).show()
                }

                override fun getUserProfile(userProfile: UserProfileModel?) {/*Unnecessary in this case */}
            })
        }

        return rootView
    }

}