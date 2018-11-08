package production.toth.attila.homesecurityinkotlin.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import production.toth.attila.homesecurityinkotlin.models.ChangePasswordModel
import production.toth.attila.homesecurityinkotlin.models.UserLoginModel
import production.toth.attila.homesecurityinkotlin.models.UserProfileModel
import production.toth.attila.homesecurityinkotlin.models.UserSignUpModel
import retrofit2.Call
import retrofit2.http.*

interface RetrofitUploadService {
    @Multipart
    @POST("api/upload/")
    abstract fun postImage(@Part image: MultipartBody.Part, @Part("name") name: RequestBody): Call<ResponseBody>

    @POST("api/account/login")
    abstract fun login(@Body loginModel: UserLoginModel): Call<ResponseBody>

    @POST("api/account/signup")
    abstract fun signUp(@Body signUpModel: UserSignUpModel): Call<ResponseBody>

    @POST("api/account/logout")
    abstract fun logOut() : Call<ResponseBody>

    @GET("api/account/profile")
    abstract fun profile(): Call<UserProfileModel>

    @GET("api/account/changepassword")
    abstract fun changePassword(@Body changePasswordModel: ChangePasswordModel): Call<ResponseBody>
}