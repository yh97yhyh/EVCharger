package com.example.myevstation_android.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import com.example.myevstation_android.model.Favorite;
import com.example.myevstation_android.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME= "DBPref";

    private static final String MY_FAVORITE_LIST = "my_favorite_list";
    private static final String IS_USER_LOGIN = "is_user_login";
    private static final String USER_ID = "user_id";
    private static final String USER_NICKNAME = "user_nickname";
    private static final String USER_UID = "user_uid";

    public DBManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addFavoriteList(Favorite favorite){
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<Favorite>>() {}.getType();

        List<Favorite> favorites = getFavoriteList();
        favorites.add(favorite);
        String json = gson.toJson(favorites,listType);
        editor.putString(MY_FAVORITE_LIST,json);
        editor.commit();
    }

    public void removeFavorite(Favorite favorite){
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<Favorite>>() {}.getType();

        List<Favorite> favorites = getFavoriteList();
        for(int i = 0 ; i < favorites.size() ; i++){
            if(favorites.get(i).getStationName().equals(favorite.getStationName())){
                favorites.remove(i);
            }
        }
        String json = gson.toJson(favorites, listType);
        editor.putString(MY_FAVORITE_LIST, json);
        editor.commit();
    }

    public List<Favorite> getFavoriteList(){
        String strFavorites = pref.getString(MY_FAVORITE_LIST,"def");
        List<Favorite> favorites;
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<Favorite>>() {}.getType();

        if(strFavorites.equals("def")){
            favorites = new ArrayList<>();
        }else{
            favorites = gson.fromJson(strFavorites,listType);
        }

        return favorites;
    }

    public void userUpdate(User user){
        editor.putString(USER_ID,user.getUserId());
        editor.putString(USER_NICKNAME,user.getUserNickname());
        editor.putInt(USER_UID,user.getUserUid());
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.commit();
    }

    public void logout(){
        editor.remove(USER_ID);
        editor.remove(USER_NICKNAME);
        editor.remove(USER_UID);
        editor.putBoolean(IS_USER_LOGIN,false);
        editor.commit();
    }

    public boolean isUserLogin(){
        return pref.getBoolean(IS_USER_LOGIN,false);
    }

    public String getUserId(){
        return pref.getString(USER_ID,"default");
    }

    public String getUserNickname(){
        return pref.getString(USER_NICKNAME,"default");
    }

    public Integer getUserUid(){
        return pref.getInt(USER_UID,0);
    }


}
