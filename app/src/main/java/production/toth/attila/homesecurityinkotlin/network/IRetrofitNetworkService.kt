package production.toth.attila.homesecurityinkotlin.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import production.toth.attila.homesecurityinkotlin.models.*
import retrofit2.Call
import retrofit2.http.*

interface IRetrofitNetworkService {

    @Multipart
    @POST("api/upload/")
    fun postImage(@Part imageFile: MultipartBody.Part, @Part("isNotifiableByEmail") isNotifiableByEmail: RequestBody ): Call<ResponseBody>

    @POST("api/account/login")
    fun login(@Body loginModel: UserLoginModel): Call<ResponseBody>

    @POST("api/account/signUp")
    fun signUp(@Body signUpModel: UserSignUpModel): Call<ResponseBody>

    @POST("api/account/logout")
    fun logOut() : Call<ResponseBody>

    @GET("api/profile")
    fun profile(): Call<ResponseBody>

    @PUT("api/profile")
    fun profileUpdate(@Body model: UserProfileUpdateModel): Call<ResponseBody>

    @GET("api/account/changepassword")
    fun changePassword(@Body changePasswordModel: ChangePasswordModel): Call<ResponseBody>

    @DELETE ("api/account/deleteaccount")
    fun deleteAccount(): Call<ResponseBody>
}