package com.inha.androidcatfood;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FoodSpotBoardActivity extends AppCompatActivity {
    public class BoardRecyclerItem {
        boolean is_notice;
        String subject;
        String content;
        String created;
        String user_id;

        String getSubject() {
            return this.subject;
        }

        String getContent() {
            return this.content;
        }

        String getCreated() {
            return this.content;
        }

        String getUser_id() {
            return this.content;
        }

        BoardRecyclerItem(boolean is_notice, String subject, String content, String created, String user_id) {
            this.is_notice = is_notice;
            this.subject = subject;
            this.content = content;
            this.created = created;
            this.user_id = user_id;
        }
    }

    public class BoardRecyclerAdapter extends RecyclerView.Adapter<com.inha.androidcatfood.FoodSpotBoardActivity.BoardRecyclerAdapter.ViewHolder> {
        Context context;
        List<BoardRecyclerItem> items;
        int item_layout;
        public BoardRecyclerAdapter(Context context, List<BoardRecyclerItem> items, int item_layout) {
            this.context = context;
            this.items = items;
            this.item_layout = item_layout;
        }

        @Override
        public com.inha.androidcatfood.FoodSpotBoardActivity.BoardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);
            return new com.inha.androidcatfood.FoodSpotBoardActivity.BoardRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(com.inha.androidcatfood.FoodSpotBoardActivity.BoardRecyclerAdapter.ViewHolder holder, int position) {
            final BoardRecyclerItem item = items.get(position);

            //item make 해주어야함
//            new DownloadImageSetter(holder.image).execute(item.getImage());
//            holder.title.setText(item.getTitle());
//            holder.gender.setText(item.gender > 0 ? "♂" : "♀");
//            holder.natrual.setText(item.natural ? "중성화 O" : "중성화 X");
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView image;
//            TextView title;
//            CardView cardview;
//            TextView gender;
//            TextView natrual;

            public ViewHolder(View itemView) {
                super(itemView);
                //view도 만들어 줘야댐
//                image=(ImageView)itemView.findViewById(R.id.image);
//                title=(TextView)itemView.findViewById(R.id.title);
//                cardview=(CardView)itemView.findViewById(R.id.cardview);
//                gender = (TextView)itemView.findViewById(R.id.gender);
//                natrual = (TextView)itemView.findViewById(R.id.netural);

            }
        }
    }

    RecyclerView recyclerView;

    APICallback getBoardCallback = new APICallback() {
        @Override
        public void run(Object arg) {

            Log.e("Test", "Get boardContents callback");

            APIClient.Board  board = (APIClient.Board) arg;

            List<BoardRecyclerItem> boardItems = new ArrayList<>();
            if (board.result != "ok") {
                Toast.makeText(getApplicationContext(), "문제가 발생했습니다. 잠시후 다시 시도 해주세요.", Toast.LENGTH_LONG).show();
                finish();
            }

            for (APIClient.BoardContent content : board.content_list) {

                boardItems.add(new BoardRecyclerItem(content.is_notice, content.subject,  content.content, content.created, content.user_id));
            }

            recyclerView.setAdapter(new BoardRecyclerAdapter(getApplicationContext(), boardItems, R.layout.activity_food_spot_board));
//
//
//            new DownloadImageSetter((ImageView) findViewById(R.id.centerImage)).execute(centerInfo.center_info.image_path);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_spot_board);
    }
}


