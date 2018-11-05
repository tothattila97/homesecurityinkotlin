package production.toth.attila.homesecurityinkotlin.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import production.toth.attila.homesecurityinkotlin.models.UserLoginModel
import production.toth.attila.homesecurityinkotlin.models.UserSignUpModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
}