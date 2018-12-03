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
import production.toth.attila.homesecurityinkotlin.models.UserProfileModel
import production.toth.attila.homesecurityinkotlin.models.UserSignUpModel
import production.toth.attila.homesecurityinkotlin.network.IHttpCallback
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService
import java.text.DateFormat
import java.util.*


class SignUpActivity() : AppCompatActivity() {

    companion object {
        val TAG = "SignUpActivity"
    }

    lateinit var usernameText: EditText
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var confirmPasswordText: EditText
    lateinit var phoneNumber: EditText
    lateinit var dateOfBirth: EditText
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var genderRadioGroup: RadioGroup
    lateinit var manGenderRadio: RadioButton
    lateinit var womanGenderRadio: RadioButton
    lateinit var notBinaryGenderRadio: RadioButton
    lateinit var signUpButton: Button
    lateinit var loginLink: TextView
    var birthCalendar = Calendar.getInstance()
    var dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        usernameText= findViewById(R.id.input_username)
        emailText = findViewById(R.id.input_email)
        passwordText= findViewById(R.id.input_password)
        confirmPasswordText = findViewById(R.id.input_confirm_password)
        phoneNumber = findViewById(R.id.input_notifiable_phonenumber)
        dateOfBirth = findViewById(R.id.input_dateOfBirth)
        firstName = findViewById(R.id.input_firstName)
        lastName = findViewById(R.id.input_lastName)
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
            val dialog = DatePickerDialog(this, date, birthCalendar.get(Calendar.YEAR), birthCalendar.get(Calendar.MONTH),
                    birthCalendar.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
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

        val name = usernameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        val confirmPassword = confirmPasswordText.text.toString()
        val phoneNumber = phoneNumber.text.toString()
        val firstName = firstName.text.toString()
        val lastName = lastName.text.toString()
        val dateOfBirth = convertStringToDate(dateOfBirth.text.toString())
        var gender: Gender = Gender.Default
        when(genderRadioGroup.checkedRadioButtonId){
            R.id.gender_man -> gender = Gender.Man
            R.id.gender_woman -> gender = Gender.Woman
            R.id.gender_notBinary -> gender = Gender.NotBinary
        }

        // TODO: Implement your own signup logic here.
        val signUpService = RetrofitNetworkService(baseContext)
        val signUpModel = UserSignUpModel(email, name, password,confirmPassword,phoneNumber, dateOfBirth, gender, firstName, lastName)
        signUpService.signup(signUpModel, object : IHttpCallback {
            override fun getIsSucceeded(succeeded: Boolean) {
                if(succeeded){
                    Handler().postDelayed(
                            {
                                // On complete call either onSignupSuccess or onSignupFailed
                                // depending on success
                                onSignUpSuccess()
                                // onSignupFailed();
                                progressDialog.dismiss()
                            }, 2000)
                }
                else
                    Toast.makeText(baseContext, "Registration failed. Try again!", Toast.LENGTH_SHORT).show()
            }

            override fun getUserProfile(userProfile: UserProfileModel?) {}
        })
    }

     private fun onSignUpSuccess() {
         signUpButton.isEnabled = true
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onSignUpFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
        signUpButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val name = usernameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        val confirmPassword = confirmPasswordText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            usernameText.error = "at least 3 characters"
            valid = false
        } else {
            usernameText.error = null
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
        return dateFormat.format(date)
    }

    private fun convertStringToDate(timeInString : String): Date{
        return dateFormat.parse(timeInString)
    }
}
