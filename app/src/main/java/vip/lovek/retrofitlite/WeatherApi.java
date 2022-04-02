package vip.lovek.retrofitlite;

import okhttp3.Call;
import vip.lovek.retrofit.annotation.Field;
import vip.lovek.retrofit.annotation.GET;
import vip.lovek.retrofit.annotation.POST;
import vip.lovek.retrofit.annotation.Query;

/**
 * author： yuzhirui@douban.com
 * date: 2022-04-02 09:33
 */
interface WeatherApi {

    @POST("/v3/weather/weatherInfo")
    Call postWeather(@Field("city") String city, @Field("key") String key);

    @GET("/v3/weather/weatherInfo")
    Call getWeather(@Query("city") String city, @Query("key") String key);

}
