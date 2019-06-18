package com.inha.androidcatfood;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.JsonReader;
import android.view.Window;

import android.widget.Toast;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    GoogleMap gMap;

    APICallback getCenterCallback = new APICallback() {
        @Override
        public void run(Object arg) {
            APIClient.FoodSpotList foodSpotList = (APIClient.FoodSpotList) arg;
            //GetFoodSpotAndAddMarker(foodSpotList);

            List<APIClient.FoodSpot> _foodSpotList = foodSpotList.food_center_list;

            if (_foodSpotList == null) {
                Toast.makeText(getApplicationContext(), "문제가 발생했습니다.", Toast.LENGTH_LONG).show();
                return;
            }
            for (APIClient.FoodSpot foodSpot : _foodSpotList) {
                addMarker(gMap, foodSpot.latitude, foodSpot.longitude, foodSpot.name, foodSpot.owner, foodSpot.id);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = new Intent(this.getIntent());
        Profile fbProfile = intent.getParcelableExtra("fbProfile");

        APIClient.getInstance().getCenter(getCenterCallback);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        gMap = map;
        UiSettings mapStting = map.getUiSettings();

        mapStting.setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(this);

        LatLng SEOUL = new LatLng(37.56, 126.97);
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public void addMarker(GoogleMap map, double latitude, double longitude, String title, String snippet, String tag) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title(title);
        markerOptions.snippet(snippet);

        Marker marker = map.addMarker(markerOptions);
        marker.setTag(tag);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, FoodSpotInfoActivity.class);
        intent.putExtra("_id", marker.getTag().toString());
        startActivity(intent);
        return;
    }
}
