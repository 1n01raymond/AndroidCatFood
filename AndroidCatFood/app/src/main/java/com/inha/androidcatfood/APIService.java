package com.inha.androidcatfood;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService{

    @POST("/api/user")
    Call<ResponseBody> login(@Body APIClient.LoginRequest body);

    @POST("api/board")
    Call<APIClient.WriteBoardResult> writeBoard(@Body APIClient.WriteBoardRequest body);

    @GET("api/center")
    Call<APIClient.FoodSpotList> getCenter();

    @GET("api/center_info")
    Call<APIClient.CenterInfo> getCenterInfo(@Query("id") int centerID);

}
