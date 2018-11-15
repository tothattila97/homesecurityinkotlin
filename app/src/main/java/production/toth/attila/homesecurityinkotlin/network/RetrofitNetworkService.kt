package production.toth.attila.homesecurityinkotlin.network

import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import production.toth.attila.homesecurityinkotlin.models.ChangePasswordModel
import production.toth.attila.homesecurityinkotlin.models.UserLoginModel
import production.toth.attila.homesecurityinkotlin.models.UserSignUpModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class RetrofitNetworkService() {

    var service: IRetrofitNetworkService
    var cookie: String? = null

    init {
        val baseUrl = "https://imagestorageinblobdemo20180417110725.azurewebsites.net/"
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Saját cookie interceptor használata
        val addCookiesInterceptor = createAddCookiesInterceptor()
        val receivedCookiesInterceptor = createReceivedCookiesInterceptor()
        val client = OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(addCookiesInterceptor).addInterceptor(receivedCookiesInterceptor).build()
        service = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(IRetrofitNetworkService::class.java)
    }

    fun createAddCookiesInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            if (cookie != null) {
                requestBuilder.addHeader("Cookie", cookie)
            }
            it.proceed(requestBuilder.build())
        }
    }

    fun createReceivedCookiesInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.proceed(it.request())
            cookie = originalRequest.header("Set-Cookie")
            originalRequest
        }
    }

    fun uploadImage(file: File){
        val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)    //      image/* helyett jpg odaírva
        val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "upload_test")

        val req = service.postImage(body, name)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun login(loginModel: UserLoginModel){

        val req = service.login(loginModel)
        req.enqueue(object : Callback<okhttp3.Response> {
            override fun onResponse(call: Call<okhttp3.Response>, response: Response<okhttp3.Response>) {}
            override fun onFailure(call: Call<okhttp3.Response>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun logout(){

        val req = service.logOut()
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun signup(signUpModel: UserSignUpModel){

        val req = service.signUp(signUpModel)
        req.enqueue(object : Callback<okhttp3.Response> {
            override fun onResponse(call: Call<okhttp3.Response>, response: Response<okhttp3.Response>) {}
            override fun onFailure(call: Call<okhttp3.Response>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun profile(){

    }

    fun changePassword(changePasswordModel: ChangePasswordModel){

        val req = service.changePassword(changePasswordModel)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun deleteAccount(){

    }
}