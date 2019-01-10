package tk.jonathancowling.rxjavatoy.network

import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.*

class NetworkConfig {
    fun retrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(DEFAULT_BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // createWithScheduler is better
        .addConverterFactory(StringConverterFactory())
        .client(httpClient)
        .build()

    @Throws(RuntimeException::class)
    fun <T> extractResult(result: Result<T>): T {
        if (result.isError || result.response()?.isSuccessful != true || result.response()?.body() == null) {
            Logger.getLogger(this::class.java.simpleName).log(Level.SEVERE,
                "ERR (status: ${result.response()?.code() ?: "null"}, msg: ${result.response()?.errorBody()?.string() ?: "null"})")
            throw RuntimeException(result.error())
        } else {
            return result.response()!!.body()!!
        }
    }


    fun <T> toMainThread() = SingleTransformer<T, T> { upstream ->
        upstream.observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> configureRequest() = SingleTransformer<Result<T>, Result<T>> { upstream ->
        upstream.subscribeOn(Schedulers.io())
            .timeout(NetworkConfig.DEFAULT_TIMEOUT, NetworkConfig.DEFAULT_TIME_UNIT)
    }

    fun <T> extractResult(next: Callback<T>) = object : retrofit2.Callback<T> {
        override fun onFailure(call: Call<T>?, t: Throwable?) {
            next.onError(t)
        }

        override fun onResponse(call: Call<T>?, response: Response<T>?) {

            if (response?.isSuccessful != true || response.body() == null) {

                next.onError(RuntimeException(response?.errorBody()?.string()))
            } else {

                next.onSuccess(response.body()!!)
            }
        }
    }

    // Create a trust manager that does not validate certificate chains
    private val trustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            //chain.iterator().forEach { certificate -> certificate.checkValidity() }
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            //chain.iterator().forEach { certificate -> certificate.checkValidity() }
        }
    }

    private val httpClient: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(trustManager)

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val builder = OkHttpClient.Builder()
        // Create an ssl socket factory with our all-trusting manager
        builder.sslSocketFactory(sslContext.socketFactory, trustManager)
        builder.hostnameVerifier { hostname, session -> session.isValid && hostname == DEFAULT_HOST }
        builder.build()
    }

    val cache = DemoApiCache(DEFAULT_CACHE_TIME)

    companion object {
        const val DEFAULT_CACHE_TIME = 10000L
        const val DEFAULT_TIMEOUT = 30L
        val DEFAULT_TIME_UNIT = TimeUnit.SECONDS
        private const val DEFAULT_HOST = "vbdy18l258.execute-api.eu-west-1.amazonaws.com"
        const val DEFAULT_BASE_URL = "https://$DEFAULT_HOST/default/hello_world/"
    }

    class DemoApiCache(private val timeToCache: Long) : DemoApi {

        private val rxMap = mutableMapOf<Int, Pair<Long, String>>()
        private val cbMap = mutableMapOf<Int, Pair<Long, String>>()

        override fun getDocuments() = getFromRxMap(DOCS_KEY)
        override fun getProfile() = getFromRxMap(PROFILE_KEY)

        fun setDocuments(result: String) {
            rxMap[DOCS_KEY] = Pair(System.currentTimeMillis(), result)
        }

        fun setProfile(result: String) {
            rxMap[PROFILE_KEY] = Pair(System.currentTimeMillis(), result)
        }

        override fun getDocumentsCallback(): Call<String> {

            return getFromCbMap(DOCS_KEY)
        }

        override fun getProfileCallback(): Call<String> {

            return getFromCbMap(PROFILE_KEY)
        }


        private fun getFromRxMap(key: Int): Single<Result<String>> =
            rxMap[key]?.let {
                if (!isOutOfDate(it.first)) {
                    return@let Single.just(Result.response(Response.success(it.second)))
                } else {
                    rxMap.clear()
                    return@let null
                }
            } ?: Single.error(NullPointerException())

        private fun getFromCbMap(key: Int): Call<String> {

            return object : Call<String> {

                @Throws(NotImplementedError::class)
                override fun clone(): Call<String> { throw NotImplementedError() }

                @Throws(NotImplementedError::class)
                override fun isCanceled(): Boolean { throw NotImplementedError() }

                @Throws(NotImplementedError::class)
                override fun cancel() { throw NotImplementedError() }

                @Throws(NotImplementedError::class)
                override fun request(): Request { throw NotImplementedError() }

                private var _isExecuted = false
                override fun isExecuted() = _isExecuted

                override fun enqueue(callback: retrofit2.Callback<String>?) {
                    val response = this.execute()
                    if (response.isSuccessful && response.body() != null) {
                        callback?.onResponse(this, response)
                    } else {
                        callback?.onFailure(this, RuntimeException(response.errorBody()?.string()))
                    }
                }

                override fun execute(): Response<String>  = cbMap[key]?.let {
                    _isExecuted = true
                    if (!isOutOfDate(it.first)) {
                        Response.success(it.second)
                    } else {
                        cbMap.clear()
                        Response.error(404, ResponseBody.create(MediaType.parse("text"), "Not Found"))
                    }
                } ?: Response.error(500, ResponseBody.create(MediaType.parse("text"), "Internal Server Error"))
            }


        }

        private fun isOutOfDate(millis: Long) = millis < System.currentTimeMillis() + timeToCache

        companion object {
            private const val DOCS_KEY = 0
            private const val PROFILE_KEY = 1
        }
    }
}