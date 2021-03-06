package com.tylercarberry.magicmirror

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tylercarberry.magicmirror.calendar.CalendarService
import com.tylercarberry.magicmirror.gas.GasolineService
import com.tylercarberry.magicmirror.inflation.InflationService
import com.tylercarberry.magicmirror.news.NewsService
import com.tylercarberry.magicmirror.news.NyTimesApi
import com.tylercarberry.magicmirror.weather.WeatherApi
import com.tylercarberry.magicmirror.weather.WeatherService
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App: Application() {

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        val appModule = module {
            single { provideGson() }
            single { provideWeatherApi(get()) }
            single { provideNewsApi(get()) }
            single { provideOkHttpClient() }
            single { provideCalendarService(get()) }
            single { provideGasolineService(get()) }
            single { provideInflationService(get()) }
            single { provideNewsService(get()) }
            single { provideWeatherService(get()) }
            viewModel<MainViewModel> { MainViewModelImpl(get(), get(), get(), get(), get()) }
        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }

        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    private fun provideCalendarService(client: OkHttpClient): CalendarService {
        return CalendarService(client)
    }

    private fun provideGasolineService(client: OkHttpClient): GasolineService {
        return GasolineService(client)
    }

    private fun provideInflationService(client: OkHttpClient): InflationService {
        return InflationService(client)
    }

    private fun provideWeatherApi(gson: Gson): WeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

    private fun provideWeatherService(weatherApi: WeatherApi): WeatherService {
        return WeatherService(weatherApi)
    }

    private fun provideNewsApi(gson: Gson): NyTimesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(NyTimesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(NyTimesApi::class.java)
    }

    private fun provideNewsService(api: NyTimesApi): NewsService {
        return NewsService(api)
    }
}
