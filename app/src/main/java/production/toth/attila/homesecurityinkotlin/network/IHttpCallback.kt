package production.toth.attila.homesecurityinkotlin.network

import production.toth.attila.homesecurityinkotlin.models.UserProfileModel

interface IHttpCallback{
    fun getIsSucceeded(succeeded: Boolean)
    fun getUserProfile(userProfile: UserProfileModel?)
}