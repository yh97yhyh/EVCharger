package com.example.myevstation_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myevstation_android.db.DBManager;
import com.example.myevstation_android.model.User;
import com.example.myevstation_android.remote.ApiService;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private Context context = this;

    private DBManager dbManager;
    private ApiService service;

    private TextView userName, userId,logout;
    private EditText userIdEditText, userPwEditText, userNickNameEditText;
    private Button registerButton, loginButton;

    private ConstraintLayout userLayout, registerLayout;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbManager = new DBManager(context);
        service = ((GlobalApplication) getApplication()).getApiService();
        progressBar = findViewById(R.id.progress_bar);

        userLayout = findViewById(R.id.user_layout);
        registerLayout = findViewById(R.id.user_register_layout);

        userName = findViewById(R.id.user_name);
        userId = findViewById(R.id.user_id);
        logout = findViewById(R.id.logout);
        userIdEditText = findViewById(R.id.register_user_id_edit);
        userPwEditText = findViewById(R.id.register_user_pw_edit);
        userNickNameEditText = findViewById(R.id.register_user_nickname_edit);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);

        if(dbManager.isUserLogin()){
            registerLayout.setVisibility(View.GONE);
            userLayout.setVisibility(View.VISIBLE);

            userName.setText(dbManager.getUserNickname());
            userId.setText(dbManager.getUserId());
            logout.setOnClickListener(v->{
                logoutUser();
            });
        }else{
            registerLayout.setVisibility(View.VISIBLE);
            userLayout.setVisibility(View.GONE);


            loginButton.setOnClickListener(v->{
                loginUser();
            });

            registerButton.setOnClickListener(v->{
                createUser();
            });
        }
    }

    private void logoutUser(){
        dbManager.logout();
        finish();
    }

    private void loginUser(){
        if(!userIdEditText.getText().toString().isEmpty() && !userPwEditText.getText().toString().isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            service.loginUser(userIdEditText.getText().toString(), userPwEditText.getText().toString()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            dbManager.userUpdate(response.body());
                        }
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }else{
                        Toast.makeText(context,"아이디와 비밀번호를 올바르게 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });

        }else{
            Toast.makeText(context,"아이디와 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser(){
        if(!userIdEditText.getText().toString().isEmpty() && !userPwEditText.getText().toString().isEmpty() && !userNickNameEditText.getText().toString().isEmpty()){
            progressBar.setVisibility(View.VISIBLE);

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uid", userIdEditText.getText().toString())
                    .addFormDataPart("passwd", userPwEditText.getText().toString())
                    .addFormDataPart("nickname", userNickNameEditText.getText().toString())
                    .build();

            service.createUser(body).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    progressBar.setVisibility(View.GONE);
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            dbManager.userUpdate(response.body());
                        }
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }else{
                        Toast.makeText(context,"존재하는 아이디 입니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });

        }else{
            Toast.makeText(context,"정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }
}