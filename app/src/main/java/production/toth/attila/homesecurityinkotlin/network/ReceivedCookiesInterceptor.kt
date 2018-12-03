package production.toth.attila.homesecurityinkotlin.network

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class ReceivedCookiesInterceptor(private val context: Context) // AddCookiesInterceptor()
    : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = PreferenceManager.getDefaultSharedPreferences(context).getStringSet("PREF_COOKIES", HashSet()) as HashSet<String>
            cookies.clear()

            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }

            val memes = PreferenceManager.getDefaultSharedPreferences(context).edit()
            memes.putStringSet("PREF_COOKIES", cookies).apply()
            memes.commit()
        }

        return originalResponse
    }
}