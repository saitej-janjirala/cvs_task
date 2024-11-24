package com.saitejajanjirala.cvs_task.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.saitejajanjirala.cvs_task.data.db.DatabaseService
import com.saitejajanjirala.cvs_task.data.db.SearchDao
import com.saitejajanjirala.cvs_task.data.remote.ApiService
import com.saitejajanjirala.cvs_task.util.Util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun getOkHttpClient(@ApplicationContext context: Context): OkHttpClient{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(InternetInterceptor(context))
            addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val originalRequest = chain.request()
                    val originalUrl = originalRequest.url
                    val newRequest=originalRequest.newBuilder().apply {
                        url(originalUrl.newBuilder()
                            .addQueryParameter("format","json")
                            .addQueryParameter("nojsoncallback","1")
                           .build())
                    }.build()
                    return chain.proceed(newRequest)
                }
            })
        }
        httpClient.addInterceptor(logging)
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun providesApiService(moshi: Moshi,okHttpClient: OkHttpClient): ApiService{
        return  Retrofit.Builder()
            .baseUrl(Util.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun providesDatabase(application: Application):DatabaseService{
        return Room.databaseBuilder(application, DatabaseService::class.java,DatabaseService.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesItemDao(databaseService: DatabaseService): SearchDao {
        return databaseService.searchDao
    }

}