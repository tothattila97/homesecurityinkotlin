package production.toth.attila.homesecurityinkotlin.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import production.toth.attila.homesecurityinkotlin.R
import java.text.DateFormat
import java.util.*


class SignUpActivity : AppCompatActivity() {

    companion object {
        val TAG = "SignUpActivity"
    }

    lateinit var nameText: EditText
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var confirmPasswordText: EditText
    lateinit var dateOfBirth: EditText
    lateinit var genderRadioGroup: RadioGroup
    lateinit var manGenderRadio: RadioButton
    lateinit var womanGenderRadio: RadioButton
    lateinit var notBinaryGenderRadio: RadioButton
    lateinit var signUpButton: Button
    lateinit var loginLink: TextView
    var birthCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameText= findViewById(R.id.input_name)
        emailText = findViewById(R.id.input_email)
        passwordText= findViewById(R.id.input_password)
        confirmPasswordText = findViewById(R.id.input_confirm_password)
        dateOfBirth = findViewById(R.id.input_dateOfBirth)
        genderRadioGroup = findViewById(R.id.gender_radioGroup)
        manGenderRadio = findViewById(R.id.gender_man)
        womanGenderRadio = findViewById(R.id.gender_woman)
        notBinaryGenderRadio = findViewById(R.id.gender_notBinary)
        signUpButton= findViewById(R.id.btn_signup)
        loginLink = findViewById(R.id.link_login)

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            birthCalendar.set(Calendar.YEAR, year)
            birthCalendar.set(Calendar.MONTH, monthOfYear)
            birthCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            refreshBirthEditText()
        }

        dateOfBirth.setOnClickListener {
            DatePickerDialog(this, date, birthCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH),
                    birthCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        signUpButton.setOnClickListener {
            signup()
        }

        loginLink.setOnClickListener {
            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun signup() {
        Log.d(TAG, "SignUp")

        if (!validate()) {
            onSignUpFailed()
            return
        }

        signUpButton.isEnabled = false

        val progressDialog = ProgressDialog(this@SignUpActivity,
                R.style.Base_Theme_AppCompat_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        // TODO: Implement your own signup logic here.

        Handler().postDelayed(
                {
                    // On complete call either onSignupSuccess or onSignupFailed
                    // depending on success
                    onSignUpSuccess()
                    // onSignupFailed();
                    progressDialog.dismiss()
                }, 3000)
    }

     private fun onSignUpSuccess() {
         signUpButton.isEnabled = true
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun onSignUpFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
        signUpButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            nameText.error = "at least 3 characters"
            valid = false
        } else {
            nameText.error = null
        }

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

    private fun refreshBirthEditText(){
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH)
        dateOfBirth.setText(dateFormat.format(birthCalendar.time))
    }
}
