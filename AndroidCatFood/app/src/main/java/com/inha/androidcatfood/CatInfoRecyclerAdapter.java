package com.inha.androidcatfood;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CatInfoRecyclerAdapter extends RecyclerView.Adapter<CatInfoRecyclerAdapter.ViewHolder> {
    Context context;
    List<CatInfoRecyclerItem> items;
    int item_layout;
    public CatInfoRecyclerAdapter(Context context, List<CatInfoRecyclerItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CatInfoRecyclerItem item = items.get(position);
        new DownloadImageSetter(holder.image).execute(item.getImage());
        holder.title.setText(item.getTitle());
        holder.gender.setText(item.gender > 0 ? "남아" : "여아");
        holder.natrual.setText(item.natural ? "중성화 O" : "중성화 X");
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CardView cardview;
        TextView gender;
        TextView natrual;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.title);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
            gender = (TextView)itemView.findViewById(R.id.gender);
            natrual = (TextView)itemView.findViewById(R.id.netural);

        }
    }
}