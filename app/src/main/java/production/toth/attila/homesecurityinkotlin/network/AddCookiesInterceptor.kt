package production.toth.attila.homesecurityinkotlin.network

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class AddCookiesInterceptor(// We're storing our stuff in a database made just for cookies called PREF_COOKIES.
        // I recommend you do this, and don't change this default value.
        private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, HashSet()) as HashSet<String>

        // Use the following if you need everything in one line.
        // Some APIs die if you do it differently.
        var cookieString = ""
        for (cookie in preferences) {
            val parser = cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            cookieString = cookieString + parser[0] + "; "
        }
        builder.addHeader("Cookie", cookieString)

        return chain.proceed(builder.build())
    }

    companion object {
        val PREF_COOKIES = "PREF_COOKIES"
    }
}