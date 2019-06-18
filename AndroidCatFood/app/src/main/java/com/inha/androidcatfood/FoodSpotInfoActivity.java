package com.inha.androidcatfood;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class FoodSpotInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_spot_info);
        String _id = getIntent().getStringExtra("_id");
        initControls();
    }

    private void initControls() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setBackground(new ShapeDrawable(new OvalShape()));
        imageView.setClipToOutline(true);
    }

}
