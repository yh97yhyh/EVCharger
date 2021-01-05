package com.example.myevstation_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myevstation_android.R;
import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {

    private Context context;
    private List<Review> dataList = new ArrayList<>();

    public ReviewListAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);
        return new ReviewListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.MyViewHolder holder, int position) {
        Review item = dataList.get(position);

        holder.userName.setText(item.getNickname());
        holder.review.setText(item.getContents());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, review;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            review = itemView.findViewById(R.id.review);

        }
    }

    public void setData(List<Review> dataList){
        if(dataList != null){
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public void clearData(){
        this.dataList.clear();;
        notifyDataSetChanged();
    }

}