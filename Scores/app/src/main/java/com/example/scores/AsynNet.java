package com.example.scores;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.CookieHandler;

import java.net.CookiePolicy;
import java.net.URL;


public class AsynNet {

    public interface Callback {
        void onResponse(String response);
    }

    public static void Login(String username,String password,final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Stores the returned array
                String[] judge=NetUtils.LoginByPost(username, password);
                //According to the page source code contains the following statement:"window.top.document.location.replace("../MAINFRM.aspx");"
                //to judge whether the login success

                int flag=judge[0].indexOf("window.top.document.location.replace(\"../MAINFRM.aspx\");");
                //If the login is successful, the cookie after the login is suc essful is returned
                if(flag!=-1) {
                    final String response = judge[1];
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                }

                //If the login fails, an error message is returned
                else if(judge[0].equals("Wrong")){
                    final String response = "Wrong";
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
            }else {
                    final String response = "wrong";
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(response);
                        }
                    });
                }
            }
        }).start();

    }

    public static void Search(String year,String term,String cookie,final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //The query is based on the information selected by the user and the cookie value
                final String response =  NetUtils.Search(year,term,cookie);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();

    }

}
