package production.toth.attila.homesecurityinkotlin.models

import java.time.ZonedDateTime

data class UserProfileModel(val email: String,
                       val userName: String,
                       val dateOfBirth: ZonedDateTime,
                       val gender: Gender)