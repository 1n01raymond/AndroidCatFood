package com.inha.androidcatfood;

import android.util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public static APIClient getInstance() {
        if (instance == null) {
            instance = new APIClient();
        }
        return instance;
    }

    private Retrofit retrofit;
    private APIService apiService;

    public APIClient() {
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

    public void login(final String id, final String name, final APICallback callback){
        Call<ResponseBody> res = apiService.login(new LoginRequest(id, name));
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.v("Test", response.body().string());

                    // 다음 화면 넘어가기
                    if(callback != null)
                        callback.run(null);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getCenter() {
        final Call<FoodSpotList> res = apiService.getCenter();
        res.enqueue(new Callback<FoodSpotList>() {
            @Override
            public void onResponse(Call<FoodSpotList> call, Response<FoodSpotList> response) {
                FoodSpotList result = response.body();
                Log.v("Test", result.result);
            }
            @Override
            public void onFailure(Call<FoodSpotList> call, Throwable t) {

            }
        });
    }

    public class LoginRequest {
        String id;
        String name;

        LoginRequest(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public class FoodSpotList {
        String result;
        List<FoodSpot> food_center_list;
    }

    public class FoodSpot {
        String id;
        String name;
        String owner;
        Double latitude;
        Double longitude;

        FoodSpot(String id, String name, String owner, Double latitude, Double longitude) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
