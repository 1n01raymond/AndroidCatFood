package com.inha.androidcatfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoodSpotInfoActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    TextView title;
    TextView admin;
    String foodSpotID;
    ArrayList<APIClient.BoardContent> board;

    APICallback getCenterInfoCallback = new APICallback() {
        @Override
        public void run(Object arg) {

            Log.e("Test", "Get centerInfo callback");

            APIClient.CenterInfo centerInfo = (APIClient.CenterInfo)arg;

            List<CatInfoRecyclerItem> items = new ArrayList<>();

            for(int i = 0; i < centerInfo.cat_list_cnt; i++){
                APIClient.CatInfo catInfo = centerInfo.cat_list.get(i);
                items.add(new CatInfoRecyclerItem(catInfo.image_path, catInfo.nickname, catInfo.gender, catInfo.is_natural));
            }

            recyclerView.setAdapter(new CatInfoRecyclerAdapter(getApplicationContext(),items,R.layout.activity_food_spot_info));

            title.setText(centerInfo.center_info.name);
            admin.setText("관리자 : " + centerInfo.center_info.owner_name);

            new DownloadImageSetter((ImageView)findViewById(R.id.centerImage)).execute(centerInfo.center_info.image_path);

            board = new ArrayList<>();
            for(APIClient.BoardContent c : centerInfo.content_list){
                board.add(c);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_spot_info);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        title = (TextView)findViewById(R.id.center_title);
        admin = (TextView)findViewById(R.id.center_admin);

        Button btnBoard = (Button)findViewById(R.id.btnBoard);
        btnBoard.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


         String _id = foodSpotID =getIntent().getStringExtra("_id");

        APIClient.getInstance().getCenterInfo(Integer.parseInt(_id), getCenterInfoCallback);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, FoodSpotBoardActivity.class);
        intent.putExtra("_id", foodSpotID);
        intent.putExtra("_board", board);
        startActivity(intent);
    }
}
