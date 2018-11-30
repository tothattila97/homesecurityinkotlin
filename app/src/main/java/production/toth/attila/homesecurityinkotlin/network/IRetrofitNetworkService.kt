package production.toth.attila.homesecurityinkotlin.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import production.toth.attila.homesecurityinkotlin.models.*
import retrofit2.Call
import retrofit2.http.*

interface IRetrofitNetworkService {

    @POST("api/upload/")
    abstract fun postImage(@Body uploadModel: UploadModel): Call<ResponseBody>

    @POST("api/account/login")
    abstract fun login(@Body loginModel: UserLoginModel): Call<ResponseBody>

    @POST("api/account/signup")
    abstract fun signUp(@Body signUpModel: UserSignUpModel): Call<ResponseBody>

    @POST("api/account/logout")
    abstract fun logOut() : Call<ResponseBody>

    @GET("api/account/profile")
    abstract fun profile(): Call<ResponseBody>

    @PUT("api/account/profile")
    abstract fun profileUpdate(@Body model: UserProfileUpdateModel): Call<ResponseBody>

    @GET("api/account/changepassword")
    abstract fun changePassword(@Body changePasswordModel: ChangePasswordModel): Call<ResponseBody>

    @DELETE ("api/account/deleteaccount")
    abstract fun deleteAccount(): Call<ResponseBody>
}