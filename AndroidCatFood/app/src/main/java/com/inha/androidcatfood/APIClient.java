package com.inha.androidcatfood;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static APIClient instance;

    public static APIClient getInstance(){
        if(instance == null){
            instance = new APIClient();
        }
        return instance;
    }

    private Retrofit retrofit;
    private APIService apiService;

    public APIClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …
// add logging as last interceptor
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder().baseUrl("http://cat.dry8r3ad.com:5005")
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        apiService = retrofit.create(APIService.class);
    }

    public void login(String id, String name){
        Call<ResponseBody> res = apiService.login(new LoginRequest(id, name));
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.v("Test", response.body().string());
                    // 다음 화면 넘어가기
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getCenter(){
        Call<ResponseBody> res = apiService.getCenter();
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.v("Test", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public class LoginRequest{
        String id;
        String name;

        LoginRequest(String id, String namㄷ) {
            this.id = id;
            this.name = name;
        }
    }
}
