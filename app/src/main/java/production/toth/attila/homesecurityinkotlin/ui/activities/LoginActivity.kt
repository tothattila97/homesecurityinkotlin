package production.toth.attila.homesecurityinkotlin.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.models.UserLoginModel
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService

class LoginActivity() : AppCompatActivity() {

    companion object {
        val TAG = "LoginActivity"
        val REQUEST_SIGNUP = 0
    }

    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var loginButton: Button
    lateinit var signUpLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailText = findViewById(R.id.input_email)
        passwordText= findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)
        signUpLink = findViewById(R.id.link_signup)

        loginButton.setOnClickListener {
            login()
            //val cameraIntent = Intent(applicationContext, TestActivity::class.java)
            //startActivity(cameraIntent)
        }

        signUpLink.setOnClickListener {
            val signUpIntent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(signUpIntent, REQUEST_SIGNUP)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        loginButton.isEnabled = false

        val progressDialog = ProgressDialog(this@LoginActivity,
                R.style.Base_Theme_AppCompat_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating...")
        progressDialog.show()

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        // TODO: Implement your own authentication logic here.
        val loginService = RetrofitNetworkService()
        val loginModel = UserLoginModel(email,password)
        val result = loginService.login(loginModel)

        Handler().postDelayed(
                {
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess()
                    // onLoginFailed();
                    progressDialog.dismiss()
                }, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: Implement successful signUp logic here , SharedPreferencesbe menteni a felhasználó adatait és átnavigálni a CameraActivityre
                // By default we just finish the Activity and log them in automatically
                val userLogin = getSharedPreferences("userLogin", Context.MODE_PRIVATE)
                val editor  = userLogin.edit()
                editor.clear()
                editor.putString("userName", emailText.text.toString())
                editor.putString("password", passwordText.text.toString())
                editor.apply()  // editor.commit()
                val cameraIntent = Intent(applicationContext, TestActivity::class.java)
                startActivity(cameraIntent)
                //this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true)
    }

    //TODO: Sikeres bejelentkezés esetén a felhasználó adatait SharedPreferencesbe kell tenni
    private fun onLoginSuccess() {
        loginButton.isEnabled = true
        val userLogin = getSharedPreferences("userLogin", Context.MODE_PRIVATE)
        val editor  = userLogin.edit()
        editor.clear()
        editor.putString("userName", emailText.text.toString())
        editor.putString("password", passwordText.text.toString())
        editor.apply()  // editor.commit()
        val cameraIntent = Intent(applicationContext, TestActivity::class.java)
        startActivity(cameraIntent)
        finish()
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
        loginButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "enter a valid email address"
            valid = false
        } else {
            emailText.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            passwordText.error = null
        }
        return valid
    }
}
