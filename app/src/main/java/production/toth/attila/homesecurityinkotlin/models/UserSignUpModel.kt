package production.toth.attila.homesecurityinkotlin.models

import java.util.*

data class UserSignUpModel(val email: String,
                           val userName: String,
                           val password: String,
                           val confirmPassword: String,
                           val notifiablePhoneNumber: String,
                           val dateOfBirth: Date,
                           val gender: Gender)