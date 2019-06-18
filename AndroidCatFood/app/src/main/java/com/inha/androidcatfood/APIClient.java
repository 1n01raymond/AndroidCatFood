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

    public void getCenter(final APICallback callback) {
        final Call<FoodSpotList> res = apiService.getCenter();
        res.enqueue(new Callback<FoodSpotList>() {
            @Override
            public void onResponse(Call<FoodSpotList> call, Response<FoodSpotList> response) {
                FoodSpotList result = response.body();
                Log.v("Test", result.result);
                if(callback != null)
                    callback.run(result);
            }

            @Override
            public void onFailure(Call<FoodSpotList> call, Throwable t) {

            }
        });
    }

    public void getBoard(int centerID, final APICallback callback) {
        final Call<Board> res = apiService.getBoard(centerID);
        res.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
                Board result = response.body();
                Log.v("Test", result.result);

                if(callback != null)
                    callback.run(result);
            }
            @Override
            public void onFailure(Call<Board> call, Throwable t) {

            }
        });
    }

    public void getCenterInfo(int centerID, final APICallback callback) {
        final Call<CenterInfo> res = apiService.getCenterInfo(centerID);
        res.enqueue(new Callback<CenterInfo>() {
            @Override
            public void onResponse(Call<CenterInfo> call, Response<CenterInfo> response) {
                CenterInfo result = response.body();
                Log.v("Test", result.result);

                if(callback != null)
                    callback.run(result);
            }
            @Override
            public void onFailure(Call<CenterInfo> call, Throwable t) {
                Log.v("Test", t.getLocalizedMessage());

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
        String owner_name;
        Double latitude;
        Double longitude;
        String image_path;

        FoodSpot(String id, String name, String owner_name, Double latitude, Double longitude, String image_path) {
            this.id = id;
            this.name = name;
            this.owner_name = owner_name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.image_path = image_path;
        }
    }

    public class Board{
        String result;
        List<BoardContent> content_list;
    }

    public class BoardContent{
        String id;
        String belong_center_id;
        String subject;
        boolean is_notice;
        String content;
        String created;
        String user_id;

        BoardContent(String id, String belong_center_id, String subject, boolean is_notice,
                     String content, String created, String user_id){
            this.id = id;
            this.belong_center_id = belong_center_id;
            this.subject = subject;
            this.is_notice = is_notice;
            this.content = content;
            this.created = created;
            this.user_id = user_id;
        }
    }

    public class CatInfo{
        String id;
        String nickname;
        String belong_center;
        String user_id;
        boolean is_natural;
        int gender;
        String image_path;
    }

    public class CenterInfo{
        String result;
        int cat_list_cnt;
        List<CatInfo> cat_list;
        FoodSpot center_info;

        CenterInfo(String result, int cat_list_cnt, List<CatInfo> cat_list, FoodSpot center_info){
            this.result = result;
            this.cat_list_cnt = cat_list_cnt;
            this.cat_list = cat_list;
            this.center_info = center_info;
        }
    }
}
