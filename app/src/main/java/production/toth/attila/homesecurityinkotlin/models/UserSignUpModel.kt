package production.toth.attila.homesecurityinkotlin.models

import java.util.*

data class UserSignUpModel(val email: String,
                           val userName: String,
                           val password: String,
                           val confirmPassword: String,
                           val phoneNumber: String,
                           val dateOfBirth: Date,
                           val gender: Gender,
                           val firstName: String,
                           val lastName: String)