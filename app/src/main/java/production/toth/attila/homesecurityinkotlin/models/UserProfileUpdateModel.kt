package production.toth.attila.homesecurityinkotlin.models

data class UserProfileUpdateModel(val email: String,
                                  val userName: String,
                                  val firstName: String,
                                  val lastName: String,
                                  val phoneNumber: String)