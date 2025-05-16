import okhttp3.JavaNetCookieJar
import java.net.CookieManager

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000"

    private val cookieManager = CookieManager()

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager)) // <- ini penting buat session
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}
