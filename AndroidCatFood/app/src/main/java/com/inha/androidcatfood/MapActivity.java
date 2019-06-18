package com.inha.androidcatfood;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.JsonReader;
import android.view.Window;

import com.facebook.Profile;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent=new Intent(this.getIntent());
        Profile fbProfile = intent.getParcelableExtra("fbProfile");
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        UiSettings mapStting = map.getUiSettings();


        mapStting.setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);

        // API 에서 데이터 get 해와서 MAP 에 데이터 add 아래 코드는 임시 코드
        APIClient.getInstance().getCenter();
        GetFoodSpotAndAddMarker();
//        addMarker(map, 37.617502, 127.032104, "강북구", "송중동1", "1234");
//        addMarker(map, 37.617544, 127.031713, "강북구", "송중동2", "1235");
//        addMarker(map, 37.617923, 127.031181, "강북구", "송중동3", "1236");

        LatLng SEOUL = new LatLng(37.56, 126.97);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.617544, 127.031713)));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void addMarker(GoogleMap map, double latitude, double longitude, String title, String snippet, String tag) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title(title);
        markerOptions.snippet(snippet);

        Marker marker = map.addMarker(markerOptions);
        marker.setTag(tag);
    }

    public void GetFoodSpotAndAddMarker() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.isInfoWindowShown() == true) {
            Intent intent = new Intent(this, FoodSpotInfoActivity.class);
            intent.putExtra("_id", marker.getTag().toString());
            startActivity(intent); //액티비티 활성화
        } else {
            marker.showInfoWindow();
        }
        return false;
    }
}
