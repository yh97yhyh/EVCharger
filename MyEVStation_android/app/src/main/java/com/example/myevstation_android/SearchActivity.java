package com.example.myevstation_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myevstation_android.adapter.SearchListAdapter;
import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.Station;
import com.example.myevstation_android.remote.ApiService;
import com.google.android.gms.common.api.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private Context context = this;
    private ApiService service;

    private TextView noSearchText;
    private RecyclerView searchRecycler;
    private SearchListAdapter adapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        service = ((GlobalApplication) getApplication()).getApiService();

        progressBar = findViewById(R.id.progress_bar);
        noSearchText = findViewById(R.id.no_search_item);
        searchRecycler = findViewById(R.id.search_recycler);
        adapter = new SearchListAdapter(context, new SearchListAdapter.OnItemClick() {
            @Override
            public void onClick(Station station) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constant.LAT,station.getLat());
                resultIntent.putExtra(Constant.LON,station.getLng());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        searchRecycler.setAdapter(adapter);

        getSearch(getIntent().getStringExtra(Constant.SEARCH_TEXT));

    }

    private void getSearch(String text){
        progressBar.setVisibility(View.VISIBLE);
        service.getSearch(text).enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.isSuccessful() && response.body() != null && !response.body().isEmpty()){
                    adapter.setData(response.body());
                }else{
                    searchRecycler.setVisibility(View.GONE);
                    noSearchText.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                searchRecycler.setVisibility(View.GONE);
                noSearchText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}