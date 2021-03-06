package production.toth.attila.homesecurityinkotlin.network

import android.content.Context
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
import java.util.concurrent.TimeUnit

class RetrofitNetworkService(val context: Context) {

    private var service: IRetrofitNetworkService

    init {
        val baseUrl = "https://homesecuritythesis.azurewebsites.net/"
        val homeSecBaseUrl = "http://1787eac8.ngrok.io/"
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(AddCookiesInterceptor(context))
                .addInterceptor(ReceivedCookiesInterceptor(context))
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build()
        service = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(IRetrofitNetworkService::class.java)
    }

    fun uploadImage(file: File, emailNotific: Boolean) {
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("imageFile", file.name, reqFile)
        val newName = RequestBody.create(okhttp3.MultipartBody.FORM, emailNotific.toString())

        val uploadService = Retrofit.Builder()
                .baseUrl("https://homesecuritythesis.azurewebsites.net/")
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor())
                        .addInterceptor(AddCookiesInterceptor(context))
                        .addInterceptor(ReceivedCookiesInterceptor(context))
                        .connectTimeout(40, TimeUnit.SECONDS)
                        .readTimeout(40, TimeUnit.SECONDS)
                        .build())
                .build()
                .create(IRetrofitNetworkService::class.java)

        val req = uploadService.postImage(body, newName)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {/*Notification when upload was succeeded*/}
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

    fun signUp(signUpModel: UserSignUpModel, httpCallback: IHttpCallback) {

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

        val req = service.profile()
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