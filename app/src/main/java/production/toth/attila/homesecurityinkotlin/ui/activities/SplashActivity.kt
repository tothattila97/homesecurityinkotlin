package production.toth.attila.homesecurityinkotlin.ui.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.models.UserLoginModel
import production.toth.attila.homesecurityinkotlin.models.UserProfileModel
import production.toth.attila.homesecurityinkotlin.network.IHttpCallback
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val userLogin = getSharedPreferences("userLogin", Context.MODE_PRIVATE)
        val userName = userLogin.getString("userName","")
        val password = userLogin.getString("password","")
        if (userName != "" && password != ""){
            RetrofitNetworkService(baseContext).login(UserLoginModel(userName,password), object : IHttpCallback {
                override fun getIsSucceeded(succeeded: Boolean) {
                    if (succeeded){
                        val automaticLogInIntent = Intent(baseContext, TestActivity::class.java)
                        startActivity(automaticLogInIntent)
                    }
                    else{
                        val logInIntent = Intent(baseContext, LoginActivity::class.java)
                        startActivity(logInIntent)
                    }
                    finish()
                }
                override fun getUserProfile(userProfile: UserProfileModel?) {/* Unnecessary in this case*/}
            })
        }
        else{
            val logInIntent = Intent(baseContext, LoginActivity::class.java)
            startActivity(logInIntent)
        }

    }
}
