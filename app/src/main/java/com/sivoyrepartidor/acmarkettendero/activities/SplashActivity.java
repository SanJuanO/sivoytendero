package com.sivoyrepartidor.acmarkettendero.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.sivoyrepartidor.acmarkettendero.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferencias = getSharedPreferences("VARIABLES", Context.MODE_PRIVATE);
        final boolean LOGUEADO= preferencias.getBoolean("LOGUEADO", false);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent myintent;
                if(LOGUEADO) {
                    myintent =new Intent(getApplicationContext(), MainActivity.class);
                }else{
                    myintent =new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(myintent);
                finish();
            }
        },2000);


    }
}
