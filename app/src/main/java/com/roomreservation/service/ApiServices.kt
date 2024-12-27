package com.roomreservation.service

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object ApiServices {
    private const val API_USERNAME = "admin"
    private const val API_PASSWORD = "admin"
    private const val baseUrl = "https://roomreservation.cleverapps.io/api/"

    class BasicAuthInterceptor(val username: String, val password: String): Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain
                .request()
                .newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build()
            return chain.proceed(request)
        }
    }

    class LocalDateTimeAdapter {
        @FromJson
        fun fromJson(value: String?): LocalDateTime? {
            return value?.let {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            }
        }

        @ToJson
        fun toJson(value: LocalDateTime?): String? {
            return value?.format(DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    private val moshi = Moshi.Builder()
        .add(LocalDateTimeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            val sslContext = SSLContext.getInstance("SSL").also {
                it.init(null, arrayOf(trustManager), SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier { hostname, _ -> hostname.contains("cleverapps.io") }
            addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
        }

    private val baseClient = getUnsafeOkHttpClient().build()

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .client(baseClient)
            .baseUrl(baseUrl)
            .build()
    }

    val authApiService: AuthApiService by lazy {
        createRetrofit().create(AuthApiService::class.java)
    }

    val usersApiService: UsersApiService by lazy {
        createRetrofit().create(UsersApiService::class.java)
    }

    val bookingsApiService: BookingsApiService by lazy {
        createRetrofit().create(BookingsApiService::class.java)
    }

    val roomsApiService: RoomsApiService by lazy {
        createRetrofit().create(RoomsApiService::class.java)
    }
}