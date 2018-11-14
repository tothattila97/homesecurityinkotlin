package production.toth.attila.homesecurityinkotlin.models

data class UserProfileUpdateModel(val email: String,
                                  val username: String,
                                  val surname: String,
                                  val lastName: String,
                                  val phoneNumber: String)