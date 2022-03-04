package com.tylercarberry.magicmirror

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import biweekly.component.VEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tylercarberry.magicmirror.Utils.asOrdinal
import com.tylercarberry.magicmirror.calendar.CalendarService
import com.tylercarberry.magicmirror.news.Article
import com.tylercarberry.magicmirror.news.NewsService
import com.tylercarberry.magicmirror.weather.WeatherService
import com.tylercarberry.magicmirror.weather.WeatherWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

abstract class MainViewModel: ViewModel() {
    abstract val day: LiveData<String>
    abstract val time: LiveData<Date>
    abstract val weatherWidget: LiveData<WeatherWidget>
    abstract val calendarEvents: LiveData<List<VEvent>>
    abstract val currentArticle: LiveData<Article>
    abstract val screenBrightnessPercent: LiveData<Float>

    abstract fun setLocation(location: Location)
}

class MainViewModelImpl(
    private val weatherService: WeatherService,
    private val newsService: NewsService,
    private val calendarService: CalendarService
) : MainViewModel() {

    companion object {
        const val SECONDS_IN_A_DAY: Long = 24 * 60 * 60 * 1000

        private const val NEWS_DISPLAY_MILLISECONDS: Long = 30 * 1000

        private const val NEWS_REFRESH_MILLISECONDS: Long = 30 * 60 * 1000
        private const val CALENDAR_REFRESH_MILLISECONDS: Long = 30 * 60 * 1000
        private const val WEATHER_REFRESH_MILLISECONDS: Long = 30 * 60 * 1000
        private const val SCREEN_BRIGHTNESS_REFRESH_MILLISECONDS: Long = 5 * 60 * 1000

        private const val SCREEN_BRIGHTNESS_KEY = "screen_brightness"
    }

    private var articles: List<Article> = listOf()
    private var location: Location? = null

    override val day = MutableLiveData<String>()
    override val time = MutableLiveData<Date>()
    override val weatherWidget = MutableLiveData<WeatherWidget>()
    override val calendarEvents = MutableLiveData<List<VEvent>>()
    override val currentArticle = MutableLiveData<Article>()
    override val screenBrightnessPercent = MutableLiveData<Float>()


    init {
        initClock()
        initCalendar()
        initNews()
        initWeather()
        initScreenBrightness()
    }

    private fun initScreenBrightness() {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                refreshScreenBrightness()
                delay(SCREEN_BRIGHTNESS_REFRESH_MILLISECONDS)
            }
        }
    }

    private fun refreshScreenBrightness() {
        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                screenBrightnessPercent.postValue(Firebase.remoteConfig.getDouble(SCREEN_BRIGHTNESS_KEY).toFloat())
            }
    }

    private fun initCalendar() {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                calendarEvents.postValue(calendarService.updateCalendar())
                delay(CALENDAR_REFRESH_MILLISECONDS)
            }
        }
    }

    private fun initNews() {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                articles = newsService.getFrontPageArticles()
                articles.firstOrNull()?.let { currentArticle.postValue(it) }
                delay(NEWS_REFRESH_MILLISECONDS)
            }
        }
        showNewsOnScreen()
    }

    private fun initClock() {
        viewModelScope.launch(Dispatchers.Default) {
            while(true) {
                updateTime()
                delay(1000)
            }
        }
    }

    private fun updateTime() {
        val now = Date()
        val formatter = SimpleDateFormat("EEEE, MMMM ", Locale.US)
        val formatter2 = SimpleDateFormat("d", Locale.US)
        val formatter3 = SimpleDateFormat(" yyyy", Locale.US)

        day.postValue(formatter.format(now) + formatter2.format(now).toInt().asOrdinal() + formatter3.format(now))
        time.postValue(now)
    }

    override fun setLocation(location: Location) {
        this.location = location
        viewModelScope.launch(Dispatchers.Default) {
            updateWeather()
        }
    }

    private fun initWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                delay(WEATHER_REFRESH_MILLISECONDS)
                updateWeather()
            }
        }
    }

    private suspend fun updateWeather() {
        val weather = weatherService.updateWeather(location ?: return) ?: return
        weatherWidget.postValue(
            WeatherWidget(
                currentDegrees = weather.current.temp.roundToInt(),
                icon = weather.current.weather.firstOrNull()?.icon,
                daily = weather.daily
            )
        )
    }

    private fun showNewsOnScreen() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                for (article in articles.take(5)) {
                    currentArticle.postValue(article)
                    delay(NEWS_DISPLAY_MILLISECONDS)
                }
            }
        }
    }
}