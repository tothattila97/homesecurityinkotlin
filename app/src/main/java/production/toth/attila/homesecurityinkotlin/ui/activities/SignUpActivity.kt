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


class SignUpActivity : AppCompatActivity() {

    companion object {
        val TAG = "SignUpActivity"
    }

    private lateinit var usernameText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPasswordText: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var dateOfBirth: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var manGenderRadio: RadioButton
    private lateinit var womanGenderRadio: RadioButton
    private lateinit var notBinaryGenderRadio: RadioButton
    private lateinit var signUpButton: Button
    private lateinit var loginLink: TextView
    private var birthCalendar = Calendar.getInstance()
    private var dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        usernameText= findViewById(R.id.input_username)
        emailText = findViewById(R.id.input_email)
        passwordText= findViewById(R.id.input_password)
        confirmPasswordText = findViewById(R.id.input_confirm_password)
        phoneNumber = findViewById(R.id.input_notifiable_phoneNumber)
        dateOfBirth = findViewById(R.id.input_dateOfBirth)
        firstName = findViewById(R.id.input_firstName)
        lastName = findViewById(R.id.input_lastName)
        genderRadioGroup = findViewById(R.id.gender_radioGroup)
        manGenderRadio = findViewById(R.id.gender_man)
        womanGenderRadio = findViewById(R.id.gender_woman)
        notBinaryGenderRadio = findViewById(R.id.gender_notBinary)
        signUpButton= findViewById(R.id.btn_signUp)
        loginLink = findViewById(R.id.link_login)

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            signUp()
        }

        loginLink.setOnClickListener {
            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun signUp() {
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

        val signUpService = RetrofitNetworkService(baseContext)
        val signUpModel = UserSignUpModel(email, name, password,confirmPassword,phoneNumber, dateOfBirth, gender, firstName, lastName)
        signUpService.signUp(signUpModel, object : IHttpCallback {
            override fun getIsSucceeded(succeeded: Boolean) {
                if(succeeded){
                    Handler().postDelayed(
                            {
                                // On complete call either onSignUpSuccess or onSignUpFailed
                                // depending on success
                                onSignUpSuccess()
                                progressDialog.dismiss()
                            }, 2000)
                }
                else {
                    progressDialog.dismiss()
                    onSignUpFailed()
                }
            }

            override fun getUserProfile(userProfile: UserProfileModel?) {/*Unnecessary in this case*/}
        })
    }

     private fun onSignUpSuccess() {
         signUpButton.isEnabled = true
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onSignUpFailed() {
        Toast.makeText(baseContext, "Registration failed. Try again!", Toast.LENGTH_LONG).show()
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

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        if (password.isEmpty() || password.length < 8 || !passwordRegex.matches(password)) {
            passwordText.error = "at least 8 characters, contains 1 lower, 1 upper character and 1 alphanum"
            valid = false
        } else {
            passwordText.error = null
        }

        if(confirmPassword.isEmpty() || confirmPassword.length < 8 || !passwordRegex.matches(password)){
            confirmPasswordText.error = "at least 8 characters, contains 1 lower, 1 upper character and 1 alphanum"
            valid = false
        }
        else if(!confirmPassword.equals(password)) {
            confirmPasswordText.error = "password and confirmPassword must be the same"
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
