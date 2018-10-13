package production.toth.attila.homesecurityinkotlin.network

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class RetrofitUploadImplementation{

    var service: RetrofitUploadService

    constructor(file: File){
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        service = Retrofit.Builder().baseUrl("https://imagestorageinblobdemo20180417110725.azurewebsites.net/").client(client).build().create(RetrofitUploadService::class.java)

        val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)    //      image/* helyett jpg odaírva
        val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "upload_test")

        val req = service.postImage(body, name)
        req.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}