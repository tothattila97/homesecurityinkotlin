package production.toth.attila.homesecurityinkotlin.network

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

class RetrofitUploadImplementation() {

    var service: RetrofitUploadService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        service = Retrofit.Builder()
                    .baseUrl("https://imagestorageinblobdemo20180417110725.azurewebsites.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(RetrofitUploadService::class.java)
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
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
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
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
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