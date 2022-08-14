package com.wolfython.leafweather.api

import com.wolfython.leafweather.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    //need to add a api key from openweathermap.org in APPID=
    //signin and generate new api key
    @GET("data/2.5/weather?&units=metric&APPID=")
    fun getData(@Query("q") cityName: String): Single<WeatherModel>
}