package production.toth.attila.homesecurityinkotlin.models

import java.time.ZonedDateTime

data class UserProfileModel(val email: String,
                       val surname: String,
                       val lastName: String,
                       val phoneNumber: String,
                       val dateOfBirth: ZonedDateTime,
                       val gender: Gender)