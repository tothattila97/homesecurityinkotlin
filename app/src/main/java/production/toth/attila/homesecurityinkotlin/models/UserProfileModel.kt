package production.toth.attila.homesecurityinkotlin.models

import java.util.*

data class UserProfileModel(val email: String,
                            val surname: String,
                            val lastName: String,
                            val phoneNumber: String,
                            val dateOfBirth: Date,
                            val gender: Gender)