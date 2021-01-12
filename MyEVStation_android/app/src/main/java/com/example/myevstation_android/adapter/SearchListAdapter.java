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
import com.example.myevstation_android.model.Station;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {

    private Context context;
    private List<Station> dataList = new ArrayList<>();
    private SearchListAdapter.OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick (Station station);
    }

    public SearchListAdapter(Context context, SearchListAdapter.OnItemClick onItemClick){
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public SearchListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        return new SearchListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.MyViewHolder holder, int position) {
        Station item = dataList.get(position);

        holder.stationName.setText(item.getStationName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView stationName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.station_name);

            itemView.setOnClickListener(v->{
                if(getAdapterPosition() > -1){
                    onItemClick.onClick(dataList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setData(List<Station> dataList){
        if(dataList != null){
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }


}
