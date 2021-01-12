package com.example.myevstation_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myevstation_android.adapter.FavoriteListAdapter;
import com.example.myevstation_android.adapter.ReviewListAdapter;
import com.example.myevstation_android.db.DBManager;
import com.example.myevstation_android.dialog.CustomPopUpDialog;
import com.example.myevstation_android.dialog.ReviewDialog;
import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.Review;
import com.example.myevstation_android.model.Station;
import com.example.myevstation_android.remote.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Context context = this;
    private ApiService service;
    private DBManager dbManager;

    private NaverMap mNaverMap;
    private Location currentLocation;
    private List<Marker> markerList = new ArrayList<>();
    private boolean filter1 = false, filter2 = false, filter4 = false, filter7=false;
    private ImageView myLocationButton;

    private BottomSheetBehavior filterBottomSheetBehavior, favoriteBottomSheetBehavior, stationBottomSheetBehavior;
    private View filterBottomSheet,favoriteBottomSheet, stationBottomSheet;
    private LinearLayout touchLayout;
    private boolean isFavoriteListEmpty = true;

    private TextView createReview;

    private BackPressEditText searchEditText;
    private ImageView deleteSearchButton;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private ReviewListAdapter reviewAdapter;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int FILTER_SHEET = 0 , FAVORITE_SHEET = 1, STATION_SHEET = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //base location 강남역
        currentLocation = new Location("dummyprovider");
        currentLocation.setLatitude(37.497876);
        currentLocation.setLongitude(127.027591);

        service = ((GlobalApplication) getApplication()).getApiService();
        dbManager = new DBManager(context);
        progressBar = findViewById(R.id.progress_bar);

        // 지도 객체 생성성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        // getMapAsync를 호출하여 비동기로 OnMapReady 콜백메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        setBottomSheetBehavior();
        setSearchBar();

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile_item:
                        Intent i = new Intent(context, ProfileActivity.class);
                        startActivityForResult(i,Constant.KEY_REQUEST_PROFILE);
                        break;
                    case R.id.filter_item:
                        bottomSheetStateChange(FILTER_SHEET);
                        break;

                    case R.id.favorite_item:
                        bottomSheetStateChange(FAVORITE_SHEET);
                        break;
                }
                return true;
            }
        });

        checkLocationPermission();
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;

        CameraUpdate cameraUpdate =
                CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.5));
        mNaverMap.setMinZoom(6.5);

        mNaverMap.moveCamera(cameraUpdate);


        myLocationButton = findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(v->{
            CameraUpdate cameraUpdateMyLocation =
                    CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.5));

            mNaverMap.moveCamera(cameraUpdateMyLocation);
        });

        // 카메라가 이동 되면 호출되는 이벤트트
        mNaverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                for(Marker m : markerList){
                    m.setMap(null);
                }
                markerList.clear();
                Location camLocation = new Location("dummyprovider");
                camLocation.setLatitude(naverMap.getCameraPosition().target.latitude);
                camLocation.setLongitude(naverMap.getCameraPosition().target.longitude);
                getStations(camLocation);
            }
        });

        // 현재 위치 이동
    }

    private void addMarker(List<Station> stations){
        if(stations != null && !stations.isEmpty()){
            for(Station station : stations){
                Marker marker = new Marker();
//                marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
//                marker.setWidth(UtilFunction.dpToPx(40));
//                marker.setHeight(UtilFunction.dpToPx(50));
                marker.setPosition(new LatLng(Double.parseDouble(station.getLat()),Double.parseDouble(station.getLng())));
                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        setStationBottomSheet(station);
                        return false;
                    }
                });
                markerList.add(marker);

                marker.setMap(mNaverMap);
            }
        }
    }

    private void getStations(Location location){
        progressBar.setVisibility(View.VISIBLE);
        service.getStation(location.getLatitude(),location.getLongitude(), filter1 ? true : null,filter2 ? true : null ,
                filter4 ? true : null , filter7 ? true : null ).enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().size() > 0){
                        addMarker(response.body());
                    }
                }
                Log.e("size","size : "+response.body().size());

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                Log.e("fail","fail");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // 권환 o
                == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else { // 권한 x
            CustomPopUpDialog dialog = new CustomPopUpDialog(context, new CustomPopUpDialog.ButtonListener() {
                @Override
                public void clickButton(AlertDialog alertDialog) {
                    alertDialog.dismiss();
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CODE);
                }
            });
            dialog.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권환획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //허용
                //mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
                getMyLocation();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) { //거부
                checkLocationPermission();
            } else { // 다시 묻지 않기
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(i, PERMISSION_REQUEST_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PERMISSION_REQUEST_CODE){
            checkLocationPermission();
        }else if(requestCode == Constant.KEY_REQUEST_SEARCH){
            if(resultCode == Activity.RESULT_OK){

                String lat = data.getStringExtra(Constant.LAT);
                String lon = data.getStringExtra(Constant.LON);

                CameraUpdate cameraUpdate =
                        CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 15.5));

                mNaverMap.moveCamera(cameraUpdate);
            }
        }

    }

    private void getMyLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                List<Location> locationList = locationResult.getLocations();

                if (locationList.size() > 0) {
                    //여기서 호출
                    currentLocation = locationList.get(0);

                    CameraUpdate cameraUpdate =
                            CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.5));

                    mNaverMap.moveCamera(cameraUpdate);
                }
            }
        };

        if (locationManager.isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "내 위치 가져오기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }else{
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }else{
            Toast.makeText(context, "내 위치 가져오기 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setSearchBar(){
        searchEditText = findViewById(R.id.cu_search_edit_text);
        deleteSearchButton = findViewById(R.id.delete_button);
        deleteSearchButton.setOnClickListener(v->{
            searchEditText.setText(null);
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(this);
                handled = true;
                if(searchEditText.getText() != null && !searchEditText.getText().toString().isEmpty()) {
                    Intent i = new Intent(context,SearchActivity.class);
                    i.putExtra(Constant.SEARCH_TEXT,searchEditText.getText().toString());
                    startActivityForResult(i,Constant.KEY_REQUEST_SEARCH);
                }else{
                    Toast.makeText(context,"주소가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
            return handled;
        });

        searchEditText.setOnBackPressListener(keyCode -> {
            if (searchEditText.isFocused()) searchEditText.clearFocus();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 ) {
                    deleteSearchButton.setVisibility(View.VISIBLE);
                    deleteSearchButton.setEnabled(true);
                }else{
                    deleteSearchButton.setVisibility(View.INVISIBLE);
                    deleteSearchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setBottomSheetBehavior(){

        touchLayout = findViewById(R.id.layout_for_touch);
        touchLayout.setOnClickListener(v->{
            if(filterBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ){
                filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }else if(favoriteBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
                favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }else if(stationBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
                stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            touchLayout.setVisibility(View.INVISIBLE);
        });

        filterBottomSheet = findViewById(R.id.filter_bottom_modal_layout);
        filterBottomSheetBehavior = BottomSheetBehavior.from(filterBottomSheet);
        filterBottomSheetBehavior.setPeekHeight(0);
        filterBottomSheetBehavior.setHideable(true);
        filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        setFilterBottomSheet();

        favoriteBottomSheet = findViewById(R.id.favorite_bottom_modal_layout);
        favoriteBottomSheetBehavior = BottomSheetBehavior.from(favoriteBottomSheet);
        favoriteBottomSheetBehavior.setPeekHeight(0);
        favoriteBottomSheetBehavior.setHideable(true);
        favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        setFavoriteBottomSheet();

        stationBottomSheet = findViewById(R.id.station_bottom_modal_layout);
        stationBottomSheetBehavior = BottomSheetBehavior.from(stationBottomSheet);
        stationBottomSheetBehavior.setPeekHeight(UtilFunction.getScreenHeight((Activity) context) / 4);
        stationBottomSheetBehavior.setHideable(true);
        stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView = findViewById(R.id.review_recycler);
        reviewAdapter = new ReviewListAdapter(context);
        recyclerView.setAdapter(reviewAdapter);

    }

    private void setFilterBottomSheet(){
        TextView type1 = findViewById(R.id.type_dc_demo);
        TextView type2 = findViewById(R.id.type_ac);
        TextView type7 = findViewById(R.id.type_ac_3);
        TextView type4 = findViewById(R.id.type_dc_combo);

        type1.setOnClickListener(v->{
            setButtonClick(type1, 1);
        });

        type2.setOnClickListener(v->{
            setButtonClick(type2, 2);
        });

        type7.setOnClickListener(v->{
            setButtonClick(type7, 7);
        });

        type4.setOnClickListener(v->{
            setButtonClick(type4, 4);
        });
    }

    private void setButtonClick(TextView tv, int filterNum){

        Log.e("click","click");

        switch (filterNum){
            case 1 :
                if(filter1){
                    filter1 = false;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.gray_round_radius_10));
                }else{
                    filter1 = true;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_round_radius_10));
                }
                break;
            case 2 :
                if(filter2){
                    filter2 = false;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.gray_round_radius_10));
                }else{
                    filter2 = true;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_round_radius_10));
                }
                break;
            case 4 :
                if(filter4){
                    filter4 = false;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.gray_round_radius_10));
                }else{
                    filter4 = true;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_round_radius_10));
                }
                break;
            case 7 :
                if(filter7){
                    filter7 = false;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.gray_round_radius_10));
                }else{
                    filter7 = true;
                    tv.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_round_radius_10));
                }
                break;

        }

        Location camLocation = new Location("dummyprovider");
        camLocation.setLatitude(mNaverMap.getCameraPosition().target.latitude);
        camLocation.setLongitude(mNaverMap.getCameraPosition().target.longitude);

        for(Marker m : markerList){
            m.setMap(null);
        }
        markerList.clear();

        getStations(camLocation);

    }

    private void setFavoriteBottomSheet(){
        TextView noFavoriteList = favoriteBottomSheet.findViewById(R.id.no_favorite_text);
        RecyclerView favoriteRecycler = favoriteBottomSheet.findViewById(R.id.favorite_list_recycler);

        if(dbManager.getFavoriteList() != null && !dbManager.getFavoriteList().isEmpty()){
            noFavoriteList.setVisibility(View.GONE);
            favoriteRecycler.setVisibility(View.VISIBLE);
            FavoriteListAdapter adapter = new FavoriteListAdapter(context, favorite -> {
                bottomSheetStateChange(FAVORITE_SHEET);

                CameraUpdate cameraUpdate =
                        CameraUpdate.toCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(favorite.getLat()), Double.parseDouble(favorite.getLng())), 15.5));

                mNaverMap.moveCamera(cameraUpdate);
            });
            favoriteRecycler.setAdapter(adapter);
            adapter.setData(dbManager.getFavoriteList());
            favoriteBottomSheetBehavior.setPeekHeight(UtilFunction.getScreenHeight((Activity) context) / 4);
            isFavoriteListEmpty = false;
        }else{
            favoriteRecycler.setVisibility(View.GONE);
            noFavoriteList.setVisibility(View.VISIBLE);
            favoriteBottomSheetBehavior.setPeekHeight(0);
            isFavoriteListEmpty = true;
        }
    }

    private void setStationBottomSheet(Station station){
        TextView textStationName, textAddr, textChgerType, textUseTime, textBusiName, textBusiCall, addFavorite;

        textStationName = findViewById(R.id.text_statNm_marker);
        textAddr = findViewById(R.id.text_addr_marker);
        textChgerType = findViewById(R.id.text_chgerType_marker);
        textUseTime = findViewById(R.id.text_useTime_marker);
        textBusiName = findViewById(R.id.text_busiNm_marker);
        textBusiCall = findViewById(R.id.text_busiCall_marker);
        createReview = findViewById(R.id.create_review);
        addFavorite = findViewById(R.id.add_favorite);

        textStationName.setText(station.getStationName());
        textAddr.setText(station.getAddr());
        textUseTime.setText(station.getUseTime());
        textBusiName.setText(station.getBusiName());
        textBusiCall.setText(station.getBusiCall());

        String type = "DC차데모";
        switch (station.getChgerType()){
            case "01" :
                type = "DC차데모";
                break;
            case "02" :
                type = "AC완속";
                break;
            case "03" :
                type = "DC차데모, AC3상";
                break;
            case "04" :
                type = "DC콤보";
                break;
            case "05" :
                type = "DC차데모, DC콤보";
                break;
            case "06" :
                type = "DC차데모, AC3상, DC콤보";
                break;
            case "07" :
                type = "AC3상";
                break;

        }
        textChgerType.setText(type);

        createReview.setOnClickListener(v->{
            if (dbManager.isUserLogin()) {
                ReviewDialog reviewDialog = new ReviewDialog(context, new ReviewDialog.ButtonListener() {
                    @Override
                    public void clickButton(AlertDialog alertDialog, String reviewText) {
                        if(!reviewText.isEmpty()){
                            createReview(alertDialog, station.getId(), reviewText);
                        }else{
                            Toast.makeText(context,"리뷰를 입력해주세요.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                reviewDialog.execute();
            } else {
                Toast.makeText(context,"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
            }
        });

        addFavorite.setOnClickListener(v->{
            boolean add = true;

            List<Favorite> list = dbManager.getFavoriteList();

            for (int i = 0 ; i < list.size() ; i++){
                if(list.get(i).getStationName().equals(station.getStationName())){
                    add = false;
                }
            }

            if(add){
                Favorite favorite = new Favorite();
                favorite.setStationName(station.getStationName());
                favorite.setLat(station.getLat());
                favorite.setLng(station.getLng());

                dbManager.addFavoriteList(favorite);
                setFavoriteBottomSheet();
                Toast.makeText(context,"즐겨찾기에 추가되었습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"이미 추가된 주유소입니다.",Toast.LENGTH_SHORT).show();
            }
        });

        getReview(station.getId());

        bottomSheetStateChange(STATION_SHEET);

    }

    private void getReview(String stationId){
        progressBar.setVisibility(View.VISIBLE);
        service.getReviews(stationId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if(response.isSuccessful() && response.body() != null){
                    reviewAdapter.clearData();
                    reviewAdapter.setData(response.body());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void createReview(AlertDialog alertDialog, String stationId, String reviewText){
        progressBar.setVisibility(View.VISIBLE);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("statId", stationId)
                .addFormDataPart("userNick", dbManager.getUserNickname())
                .addFormDataPart("contents", reviewText)
                .build();

        service.createReview(body).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                getReview(stationId);
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                alertDialog.dismiss();
                stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void bottomSheetStateChange(int num){
        switch (num){
            case FILTER_SHEET:
                stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                if(filterBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                    filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    touchLayout.setVisibility(View.VISIBLE);
                }else{
                    filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    touchLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case FAVORITE_SHEET:
                stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                if(favoriteBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                    setFavoriteBottomSheet();
                    if(isFavoriteListEmpty) {
                        favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }else{
                        favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    touchLayout.setVisibility(View.VISIBLE);
                }else{
                    favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    touchLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case STATION_SHEET:
                favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                touchLayout.setVisibility(View.VISIBLE);
                stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View v = getCurrentFocus();
        if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            if(searchEditText.isFocused()) searchEditText.clearFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if(filterBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ){
            touchLayout.setVisibility(View.INVISIBLE);
            filterBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(favoriteBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
            touchLayout.setVisibility(View.INVISIBLE);
            favoriteBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(stationBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
            touchLayout.setVisibility(View.INVISIBLE);
            stationBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else {
            super.onBackPressed();
        }
    }

    

}