package production.toth.attila.homesecurityinkotlin.network

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
import java.util.*

class RetrofitNetworkService() {

    var service: IRetrofitNetworkService
    var cookie: String? = null

    init {
        val baseUrl = "https://imagestorageinblobdemo20180417110725.azurewebsites.net/"
        val localTestUrl =  "http://192.168.0.248:44319/"
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Saját cookie interceptor használata
        val addCookiesInterceptor = createAddCookiesInterceptor()
        val receivedCookiesInterceptor = createReceivedCookiesInterceptor()
        val client = OkHttpClient.Builder().addInterceptor(interceptor)/*.addInterceptor(addCookiesInterceptor).addInterceptor(receivedCookiesInterceptor)*/.build()
        service = Retrofit.Builder()
                .baseUrl(localTestUrl)
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

    fun uploadImage(file: File) {
        val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)    //      image/* helyett jpg odaírva
        val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "upload_test")

        val req = service.postImage(body, name)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun login(loginModel: UserLoginModel): Boolean {

        val req = service.login(loginModel)
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    succeeded = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return succeeded
    }

    fun logout(): Boolean {

        val req = service.logOut()
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                    succeeded = true
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return succeeded
    }

    fun signup(signUpModel: UserSignUpModel): Boolean {

        val req = service.signUp(signUpModel)
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    succeeded = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return succeeded
    }

    fun profile(): UserProfileModel? {

        var req = service.profile()
        var profileModel: UserProfileModel? = null
        req.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val json = JSONObject(response.body()?.string())
                        val email = json.getString("email")
                        val surName = json.getString("surName")
                        val lastName = json.getString("lastName")
                        val phoneNumber = json.getString("phoneNumber")
                        val dateOfBirth = json.get("dateOfBirth") as Date
                        val gender = json.get("gender") as Gender
                        profileModel = UserProfileModel(email, surName, lastName, phoneNumber, dateOfBirth, gender)
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                }
            }
        })
        return profileModel
    }

    fun changePassword(changePasswordModel: ChangePasswordModel): Boolean {

        val req = service.changePassword(changePasswordModel)
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    succeeded = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return succeeded
    }

    fun deleteAccount(): Boolean {

        val req = service.deleteAccount()
        var succeeded = false
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    succeeded = true
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return succeeded
    }
}