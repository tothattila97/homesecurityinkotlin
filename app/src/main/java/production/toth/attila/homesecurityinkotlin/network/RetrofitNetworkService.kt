package production.toth.attila.homesecurityinkotlin.network

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import production.toth.attila.homesecurityinkotlin.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitNetworkService(val context: Context) {

    var service: IRetrofitNetworkService
    var cookie: String? = null

    init {
        val baseUrl = "https://imagestorageinblobdemo20180417110725.azurewebsites.net/"
        val homeSecBaseUrl = "http://9916405e.ngrok.io"
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Saját cookie interceptor használata
        val addCookiesInterceptor = createAddCookiesInterceptor()
        val receivedCookiesInterceptor = createReceivedCookiesInterceptor()
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(AddCookiesInterceptor(context))
                .addInterceptor(ReceivedCookiesInterceptor(context))
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build()
        service = Retrofit.Builder()
                .baseUrl(homeSecBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(IRetrofitNetworkService::class.java)
    }

    fun createAddCookiesInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            val cookiePreference = context.getSharedPreferences("userCookie", Context.MODE_PRIVATE)
            cookie = cookiePreference.getString("actualUserCookie", "")
            if (cookie != null && cookie != "") {
                requestBuilder.addHeader("Cookie", cookie)
            }
            it.proceed(requestBuilder.build())
        }
    }

    fun createReceivedCookiesInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.proceed(it.request())
            cookie = originalRequest.header("Set-Cookie")
            val cookiePreference = context.getSharedPreferences("userCookie", Context.MODE_PRIVATE)
            val editor  = cookiePreference.edit()
            editor.clear(); editor.putString("actualUserCookie", cookie); editor.apply();
            originalRequest
        }
    }

    fun uploadImage(file: File, emailNotific: Boolean) {
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)    //      image/* helyett jpg odaírva
        val body = MultipartBody.Part.createFormData("imageFile", file.name, reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), emailNotific.toString())

        val uploadService = Retrofit.Builder()
                .baseUrl("http://9916405e.ngrok.io")
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor())
                        .addInterceptor(AddCookiesInterceptor(context))
                        .addInterceptor(ReceivedCookiesInterceptor(context))
                        .connectTimeout(40, TimeUnit.SECONDS)
                        .readTimeout(40, TimeUnit.SECONDS)
                        .build())
                .build()
                .create(IRetrofitNetworkService::class.java)


        val req = uploadService.postImage(body, name)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun login(loginModel: UserLoginModel, httpCallback: IHttpCallback) {

        val req = service.login(loginModel)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    httpCallback.getIsSucceeded(true)
                }
                else
                    httpCallback.getIsSucceeded(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun logout(httpCallback: IHttpCallback) {

        val req = service.logOut()
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    httpCallback.getIsSucceeded(true)
                }
                else
                    httpCallback.getIsSucceeded(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun signup(signUpModel: UserSignUpModel, httpCallback: IHttpCallback) {

        val req = service.signUp(signUpModel)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    httpCallback.getIsSucceeded(true)
                }
                else
                    httpCallback.getIsSucceeded(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun profile(httpCallback: IHttpCallback) {

        var req = service.profile()
        req.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val json = JSONObject(response.body()?.string())
                        val email = json.getString("email")
                        val surName = json.optString("firstName", "")
                        val lastName = json.optString("lastName", "")
                        val phoneNumber = json.optString("phoneNumber", "")
                        val birthString = json.getString("dateOfBirth")
                        val dateOfBirth = SimpleDateFormat("yyyy-MM-dd").parse(birthString)
                        val genderString = json.getString("gender")
                        val profileModel = UserProfileModel(email, surName, lastName, phoneNumber, dateOfBirth, Gender.Man)
                        httpCallback.getUserProfile(profileModel)
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }
            }
        })
    }

    fun changePassword(changePasswordModel: ChangePasswordModel, httpCallback: IHttpCallback) {

        val req = service.changePassword(changePasswordModel)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    httpCallback.getIsSucceeded(true)
                }
                else
                    httpCallback.getIsSucceeded(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun deleteAccount(httpCallback: IHttpCallback) {

        val req = service.deleteAccount()
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    httpCallback.getIsSucceeded(true)
                }
                else
                    httpCallback.getIsSucceeded(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}