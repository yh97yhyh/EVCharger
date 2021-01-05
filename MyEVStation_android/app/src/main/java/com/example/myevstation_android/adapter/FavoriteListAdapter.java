package com.example.myevstation_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myevstation_android.R;
import com.example.myevstation_android.db.DBManager;
import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.Station;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.MyViewHolder> {

    private Context context;
    private List<Favorite> dataList = new ArrayList<>();
    private OnItemClick onItemClick;

    private DBManager dbManager;

    public interface OnItemClick {
        void onClick (Favorite favorite);
    }

    public FavoriteListAdapter(Context context, OnItemClick onItemClick){
        this.context = context;
        this.onItemClick = onItemClick;
        this.dbManager = new DBManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Favorite item = dataList.get(position);

        holder.stationName.setText(item.getStationName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView stationName, removeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.station_name);
            removeButton = itemView.findViewById(R.id.remove_button);

            stationName.setOnClickListener(v->{
                if(getAdapterPosition() > -1){
                    onItemClick.onClick(dataList.get(getAdapterPosition()));
                }
            });

            removeButton.setOnClickListener(v->{
                if(getAdapterPosition() > -1){
                    dbManager.removeFavorite(dataList.get(getAdapterPosition()));
                    dataList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setData(List<Favorite> dataList){
        if(dataList != null){
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

}
