package com.tylercarberry.magicmirror

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.tylercarberry.magicmirror.calendar.CalendarAdapter
import com.tylercarberry.magicmirror.databinding.MainFragmentBinding
import com.tylercarberry.magicmirror.weather.WeatherAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("MissingPermission")
class MainFragment : BaseFragment<MainFragmentBinding>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    private val viewModel: MainViewModel by viewModel()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (Utils.hasLocationPermission(requireContext())) {
                fetchGpsLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        binding.root.keepScreenOn = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewModel()
        if (Utils.hasLocationPermission(requireContext())) {
            fetchGpsLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        activity?.window?.decorView?.setOnSystemUiVisibilityChangeListener {
            hideOnScreenNavigationButtons()
        }
        hideOnScreenNavigationButtons()
    }

    private fun initViews() {
        with(binding) {
            weatherAdapter = WeatherAdapter(listOf())
            binding.weatherRecyclerView.adapter = weatherAdapter
            weatherRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

            calendarAdapter = CalendarAdapter(listOf())
            binding.calendarRecyclerView.adapter = calendarAdapter
            calendarRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        with(viewModel) {
            day.observe(viewLifecycleOwner) {
                binding.dateTextView.text = it
            }

            time.observe(viewLifecycleOwner) {
                binding.timeTextView.text = SimpleDateFormat("h:mm", Locale.US).format(it)
                binding.secondsTextView.text = SimpleDateFormat("ss", Locale.US).format(it)
                binding.amPmTextView.text = SimpleDateFormat("a", Locale.US).format(it)
            }

            weatherWidget.observe(viewLifecycleOwner) {
                binding.degreesTextView.text = getString(R.string.weather_degrees, it.currentDegrees)

                val iconUrl = "https://openweathermap.org/img/wn/${it.icon}@2x.png"
                Glide.with(requireActivity()).load(iconUrl).into(binding.weatherImageView)

                weatherAdapter.items = it.daily.take(5)
                weatherAdapter.notifyDataSetChanged()
            }

            calendarEvents.observe(viewLifecycleOwner) {
                calendarAdapter.items = it
                calendarAdapter.notifyDataSetChanged()
            }

            currentArticle.observe(viewLifecycleOwner) {
                binding.newsHeader.text = it.title
                binding.newsBody.text = it.abstract
                binding.newsDivider.isVisible = it.title.isNotEmpty() && it.abstract.isNotEmpty()
            }
        }
    }

    private fun hideOnScreenNavigationButtons() {
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE
        )
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    fun fetchGpsLocation() {
        val cancellationSource = CancellationTokenSource()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            task.result?.let { viewModel.setLocation(it) }
        }

        fusedLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cancellationSource.token)
            .addOnSuccessListener { it?.let { viewModel.setLocation(it) } }
            .addOnFailureListener { (it as? ResolvableApiException)?.startResolutionForResult(requireActivity(), 1) }
    }

}
