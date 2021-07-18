package com.example.scores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;

public class MainActivity extends AppCompatActivity {

    private static AsynNet dct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //The login button's response function
    public void click1(View view) {
        EditText Username = findViewById(R.id.username);
        EditText Password = findViewById(R.id.password);
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected()) {
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getBaseContext(), "Student ID and password can't be empty", Toast.LENGTH_LONG).show();
            } else {
                dct = new AsynNet();
                AsynNet.Login(username, password, new AsynNet.Callback() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("wrong")){
                            Toast.makeText(getBaseContext(), "One of Student ID and password is Wrong", Toast.LENGTH_LONG).show();
                        } else if(response.equals("Wrong")){
                            Toast.makeText(getBaseContext(), "Program running error!", Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                            intent.putExtra("cookie", response);
                            startActivity(intent);
                        }
                    }
                });

            }
        }else{
            Toast.makeText(getBaseContext(), "Your phone is not connected to the Internet. Please connect to the Internet and try again", Toast.LENGTH_LONG).show();
            return;
        }

    }
}

