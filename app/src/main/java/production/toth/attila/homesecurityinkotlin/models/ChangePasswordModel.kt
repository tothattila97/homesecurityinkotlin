package production.toth.attila.homesecurityinkotlin.models

data class ChangePasswordModel(val userName: String,
                               val email: String,
                               val password: String,
                               val confirmPassword: String)