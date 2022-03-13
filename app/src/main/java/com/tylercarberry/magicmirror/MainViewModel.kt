package com.tylercarberry.magicmirror

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import biweekly.component.VEvent
import com.tylercarberry.magicmirror.Utils.asOrdinal
import com.tylercarberry.magicmirror.calendar.CalendarService
import com.tylercarberry.magicmirror.gas.GasolineService
import com.tylercarberry.magicmirror.inflation.InflationService
import com.tylercarberry.magicmirror.news.Article
import com.tylercarberry.magicmirror.news.NewsService
import com.tylercarberry.magicmirror.weather.WeatherService
import com.tylercarberry.magicmirror.weather.WeatherWidget
import kotlinx.coroutines.CoroutineDispatcher
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
    abstract val gasPrice: LiveData<Double>
    abstract val inflation: LiveData<String>
    abstract val screenBrightnessPercent: LiveData<Float>

    abstract fun setLocation(location: Location)
}

class MainViewModelImpl(
    private val weatherService: WeatherService,
    private val newsService: NewsService,
    private val calendarService: CalendarService,
    private val gasolineService: GasolineService,
    private val inflationService: InflationService
) : MainViewModel() {

    companion object {
        const val SECONDS_IN_A_DAY: Long = 24 * 60 * 60 * 1000

        private const val NEWS_DISPLAY_MILLISECONDS: Long = 30 * 1000

        private const val HALF_HOUR: Long = 30 * 60 * 1000
        private const val SIX_HOURS: Long = 30 * 60 * 1000

        private const val NEWS_REFRESH_MILLISECONDS: Long = HALF_HOUR
        private const val CALENDAR_REFRESH_MILLISECONDS: Long = HALF_HOUR
        private const val WEATHER_REFRESH_MILLISECONDS: Long = HALF_HOUR
        private const val GAS_REFRESH_MILLISECONDS: Long = SIX_HOURS
        private const val INFLATION_REFRESH_MILLISECONDS: Long = SIX_HOURS
        private const val SCREEN_BRIGHTNESS_REFRESH_MILLISECONDS: Long = 5 * 60 * 1000
        private const val CLOCK_REFRESH_MILLISECONDS: Long = 1000

        private const val SCREEN_BRIGHTNESS_KEY = "screen_brightness"
    }

    private var articles: List<Article> = listOf()
    private var location: Location? = null

    override val day = MutableLiveData<String>()
    override val time = MutableLiveData<Date>()
    override val weatherWidget = MutableLiveData<WeatherWidget>()
    override val calendarEvents = MutableLiveData<List<VEvent>>()
    override val currentArticle = MutableLiveData<Article>()
    override val gasPrice = MutableLiveData<Double>()
    override val inflation = MutableLiveData<String>()
    override val screenBrightnessPercent = MutableLiveData<Float>()


    init {
        runPeriodically(::updateTime, CLOCK_REFRESH_MILLISECONDS, Dispatchers.Default)
        runPeriodically(::refreshScreenBrightness, SCREEN_BRIGHTNESS_REFRESH_MILLISECONDS, Dispatchers.Default)

        runPeriodically(::refreshCalendar, CALENDAR_REFRESH_MILLISECONDS, Dispatchers.IO)
        runPeriodically(::gas, GAS_REFRESH_MILLISECONDS, Dispatchers.IO)
        runPeriodically(::inflation, INFLATION_REFRESH_MILLISECONDS, Dispatchers.IO)

        initNews()
        initWeather()
    }

    private fun runPeriodically(action: () -> Unit, delayMilliseconds: Long, dispatcher: CoroutineDispatcher) {
        viewModelScope.launch(dispatcher) {
            while(true) {
                action()
                delay(delayMilliseconds)
            }
        }
    }

    private fun gas() {
        viewModelScope.launch(Dispatchers.IO) {
            gasolineService.getNationalGasPrice()?.let {
                gasPrice.postValue(it)
            }
        }
    }

    private fun inflation() {
        viewModelScope.launch(Dispatchers.IO) {
            inflationService.getInflationRate()?.let {
                inflation.postValue(it)
            }
        }
    }

    private fun refreshScreenBrightness() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val brightness = if (hour in 8..21) 1.0 else 0.25
        screenBrightnessPercent.postValue(brightness.toFloat())
    }

    private fun refreshCalendar() {
        calendarEvents.postValue(calendarService.updateCalendar())
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                for (article in articles.take(5)) {
                    currentArticle.postValue(article)
                    delay(NEWS_DISPLAY_MILLISECONDS)
                }
            }
        }
    }
}