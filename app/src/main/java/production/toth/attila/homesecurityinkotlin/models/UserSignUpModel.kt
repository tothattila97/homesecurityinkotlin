package production.toth.attila.homesecurityinkotlin.models

import java.time.ZonedDateTime

data class UserSignUpModel(val email: String,
                           val userName: String,
                           val password: String,
                           val confirmPassword: String,
                           val notifiablePhoneNumber: String,
                           val dateOfBirth: ZonedDateTime,
                           val gender: Gender)