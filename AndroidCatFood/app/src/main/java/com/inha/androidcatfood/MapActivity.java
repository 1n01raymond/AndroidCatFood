package com.inha.androidcatfood;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.widget.Toast;
import com.facebook.Profile;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.util.List;

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
                addMarker(gMap, foodSpot.latitude, foodSpot.longitude, foodSpot.name, foodSpot.owner_name, foodSpot.id);
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
        markerOptions.snippet("관리자 : " + snippet);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_gmap_marker,null);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

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
