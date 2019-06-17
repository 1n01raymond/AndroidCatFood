package com.inha.androidcatfood;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService{

    @POST("/api/user")
    Call<ResponseBody> login(@Body APIClient.LoginRequest body);

}
