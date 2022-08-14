package com.wolfython.leafweather.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.wolfython.leafweather.R
import com.wolfython.leafweather.databinding.ActivityMainBinding
import com.wolfython.leafweather.viewmodel.MainViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    //view binding
    lateinit var mainBinding: ActivityMainBinding
    private lateinit var viewmodel: MainViewModel

    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewmodel = ViewModelProviders.of(this)[MainViewModel::class.java]

        var cName = GET.getString("cityName", "Chennai")?.lowercase()
        mainBinding.edtCityName.setText(cName)
        viewmodel.refreshData(cName!!)

        getLiveData()

        mainBinding.swipeRefreshLayout.setOnRefreshListener {
            mainBinding.llData.visibility = View.GONE
           mainBinding.tvError.visibility = View.GONE
            mainBinding.pbLoading.visibility = View.GONE

            var cityName = GET.getString("cityName", cName)?.lowercase()
            mainBinding.edtCityName.setText(cityName)
            viewmodel.refreshData(cityName!!)
            mainBinding.swipeRefreshLayout.isRefreshing = false
        }

        mainBinding.imgSearchCity.setOnClickListener {
            val cityName = mainBinding.edtCityName.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewmodel.refreshData(cityName)
            getLiveData()
            Log.i(TAG, "onCreate: " + cityName)
        }

    }

    private fun getLiveData() {

        viewmodel.weather_data.observe(this, Observer { data ->
            data?.let {
                mainBinding.llData.visibility = View.VISIBLE

               mainBinding.tvCityCode.text = data.sys.country.toString()
                mainBinding.tvCityName.text = data.name.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather[0].icon + "@2x.png")
                    .into(mainBinding.imgWeatherPictures)

                mainBinding.tvDegree.text = data.main.temp.toString() + "°C"

                mainBinding.tvHumidity.text = data.main.humidity.toString() + "%"
                mainBinding.tvWindSpeed.text = data.wind.speed.toString()
                mainBinding.tvLat.text = data.coord.lat.toString()
                mainBinding.tvLon.text = data.coord.lon.toString()

            }
        })

        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    mainBinding.tvError.visibility = View.VISIBLE
                    mainBinding.pbLoading.visibility = View.GONE
                    mainBinding.llData.visibility = View.GONE
                } else {
                    mainBinding.tvError.visibility = View.GONE
                }
            }
        })

        viewmodel.weather_loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    mainBinding.pbLoading.visibility = View.VISIBLE
                    mainBinding.tvError.visibility = View.GONE
                    mainBinding.llData.visibility = View.GONE
                } else {
                    mainBinding.pbLoading.visibility = View.GONE
                }
            }
        })

    }
}


