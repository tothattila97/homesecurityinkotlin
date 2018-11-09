package production.toth.attila.homesecurityinkotlin.ui.activities

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
import production.toth.attila.homesecurityinkotlin.models.Gender
import production.toth.attila.homesecurityinkotlin.models.UserSignUpModel
import production.toth.attila.homesecurityinkotlin.network.RetrofitUploadImplementation
import java.text.DateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SignUpActivity() : AppCompatActivity() {

    companion object {
        val TAG = "SignUpActivity"
    }

    lateinit var nameText: EditText
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var confirmPasswordText: EditText
    lateinit var phoneNumber: EditText
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
        phoneNumber = findViewById(R.id.input_notifiable_phonenumber)
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
            dateOfBirth.setText(convertDateToString(birthCalendar.time))
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
        val confirmPassword = confirmPasswordText.text.toString()
        val phoneNumber = phoneNumber.text.toString()
        val dateOfBirth = convertStringToZonedDateTime(dateOfBirth.text.toString())
        var gender: Gender = Gender.Default
        when(genderRadioGroup.checkedRadioButtonId){
            R.id.gender_man -> gender = Gender.Man
            R.id.gender_woman -> gender = Gender.Woman
            R.id.gender_notBinary -> gender = Gender.NotBinary
        }

        // TODO: Implement your own signup logic here.
        val signupService = RetrofitUploadImplementation()
        val signupModel = UserSignUpModel(email, name, password,confirmPassword,phoneNumber, dateOfBirth, gender)
        signupService.signup(signupModel)

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
        val confirmPassword = confirmPasswordText.text.toString()

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

        if(confirmPassword.isEmpty() || confirmPassword.length < 4 || confirmPassword.length > 10){
            confirmPasswordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        }
        else if(!confirmPassword.equals(password)) {
            confirmPasswordText.error = "password and confirmpassword must be the same"
            valid = false
        } else { confirmPasswordText.error = null}


        return valid
    }

    private fun convertDateToString(date: Date): String{
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH)
        return dateFormat.format(date)
    }

    private fun convertStringToZonedDateTime(timeInString : String): ZonedDateTime{
        val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
        val Parser = DateTimeFormatter.ofPattern(pattern)
        return ZonedDateTime.parse(timeInString, Parser)
    }
}
