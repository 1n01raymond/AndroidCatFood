package com.inha.androidcatfood;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class FoodSpotBoardActivity extends AppCompatActivity implements View.OnClickListener {


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

        String getCreateInfo() {

            return this.created + " / 작성자 : " + this.user_id;
        }


        BoardRecyclerItem(boolean is_notice, String subject, String content, String created, String user_id) {
            this.is_notice = is_notice;
            this.subject = subject;
            this.content = content;
            this.created = created;
            this.user_id = user_id;
        }
    }

    public class BoardRecyclerAdapter extends RecyclerView.Adapter<BoardRecyclerAdapter.ViewHolder> {
        Context context;
        List<BoardRecyclerItem> items;
        int item_layout;

        public BoardRecyclerAdapter(Context context, List<BoardRecyclerItem> items, int item_layout) {
            this.context = context;
            this.items = items;
            this.item_layout = item_layout;
        }

        @Override
        public BoardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_cardview, null);
            return new BoardRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(BoardRecyclerAdapter.ViewHolder holder, int position) {
            final BoardRecyclerItem item = items.get(position);
            holder.created.setText(item.getCreateInfo());
            holder.subject.setText(item.getSubject());
            holder.content.setText(item.getContent());

            if (item.is_notice == true) {
                holder.subject.setTextColor(getResources().getColor(R.color.colorBoardNoticeSubject, null));
                holder.content.setTextColor(getResources().getColor(R.color.colorBoardNoticeContent, null));
                holder.subject.setTypeface(holder.subject.getTypeface(), Typeface.BOLD);
                holder.content.setTypeface(holder.content.getTypeface(), Typeface.BOLD);
            } else {
                holder.subject.setTextColor(getResources().getColor(R.color.colorBoardSubject, null));
                holder.content.setTextColor(getResources().getColor(R.color.colorBoardContent, null));
                holder.subject.setTypeface(holder.subject.getTypeface(), Typeface.NORMAL);
                holder.content.setTypeface(holder.content.getTypeface(), Typeface.NORMAL);
            }
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardview;

            TextView subject;
            TextView content;
            TextView created;

            public ViewHolder(View itemView) {
                super(itemView);
                //view도 만들어 줘야댐
                cardview = (CardView) itemView.findViewById(R.id.cardViewBoard);
                subject = (TextView) itemView.findViewById(R.id.tvBoardSubject);
                content = (TextView) itemView.findViewById(R.id.tvBoardContent);
                created = (TextView) itemView.findViewById(R.id.tvBoardCreatInfo);

            }
        }
    }

    RecyclerView recyclerView;
    EditText etSubject;
    EditText etContent;
    TextView tvContentLength;
    CheckBox cbxNotice;

    String _id;
    ArrayList<APIClient.BoardContent> _board;
    APIClient.BoardContent tempBoardContent;

    APICallback writeBoardCallback = new APICallback() {
        @Override
        public void run(Object arg) {
            Log.e("Test", "Set boardContents callback");
            APIClient.WriteBoardResult board = (APIClient.WriteBoardResult) arg;

            if (board.result.equals("ok")) {
                APIClient.BoardContent saveData= board.content;
                _board.add(0,saveData);

                Reload();
                Toast.makeText(getApplicationContext(), "업로드에 성공했습니다.", Toast.LENGTH_LONG).show();
                setProgressBarIndeterminateVisibility(false);
                return;
            }

            Toast.makeText(getApplicationContext(), "문제가 발생했습니다. 잠시후 다시 시도 해주세요.", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_spot_board);

        etSubject = (EditText) findViewById(R.id.etSubject);
        etContent = (EditText) findViewById(R.id.etContent);
        tvContentLength = (TextView) findViewById(R.id.tvContentLength);
        cbxNotice = (CheckBox) findViewById(R.id.cbxNotice);

        etSubject.setHint("제목을 입력하세요 ");
        etContent.setHint("글 내용을 입력하세요 ");
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                tvContentLength.setText(edit.toString().length()+" / 140");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        etContent.addTextChangedListener(textWatcher);

        Button btUpload = (Button) findViewById(R.id.btUpload);
        btUpload.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.borard_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        _id = getIntent().getStringExtra("_id");
        _board= (ArrayList<APIClient.BoardContent>) getIntent().getSerializableExtra("_board");
        Reload();
    }

    public void Reload() {
        etSubject.setText("");
        etContent.setText("");
        cbxNotice.setChecked(false);
        tvContentLength.setText("0 / 140");

        recyclerView.getRecycledViewPool().clear();
        List<BoardRecyclerItem> boardItems = new ArrayList<>();

        for (APIClient.BoardContent content : _board) {
            boardItems.add(new BoardRecyclerItem((content.is_notice == 1), content.subject, content.content, content.created, content.user_id));
        }

        recyclerView.setAdapter(new BoardRecyclerAdapter(getApplicationContext(), boardItems, R.layout.activity_food_spot_board));


    }

    @Override
    public void onClick(View v) {
        boolean isNotice = cbxNotice.isChecked();

//        APIClient.getInstance().writeBoard(_id, (isNotice ? 0 : 1), etSubject.getText().toString(), etContent.getText().toString(), writeBoardCallback);

        APIClient.getInstance().writeBoard(_id, (isNotice ? 0 : 1), "tetst", "cdcdcdcdc", writeBoardCallback);

        setProgressBarIndeterminateVisibility(true);
    }
}


