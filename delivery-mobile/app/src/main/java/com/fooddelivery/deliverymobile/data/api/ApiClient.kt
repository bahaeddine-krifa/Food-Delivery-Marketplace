package com.fooddelivery.deliverymobile.data.api

import android.content.Context
import com.fooddelivery.deliverymobile.data.local.TokenProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ApiClient @Inject constructor(
    @ApplicationContext context: Context,
    private val authInterceptor: AuthInterceptor
) {

    private val retrofit: Retrofit by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(UUID::class.java, UuidTypeAdapter())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .create()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.fooddelivery.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    class UuidTypeAdapter : com.google.gson.TypeAdapter<UUID>() {
        override fun write(out: com.google.gson.stream.JsonWriter, value: UUID?) {
            out.value(value?.toString())
        }

        override fun read(reader: com.google.gson.stream.JsonReader): UUID {
            return UUID.fromString(reader.nextString())
        }
    }
}

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenProvider.getAccessToken()

        return if (accessToken != null && accessToken.isNotEmpty()) {
            val authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(authorizedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}