package com.example.chattingapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.chattingapp.databinding.ActivitySplashscreenBinding;

public class Splashscreen_Activity extends AppCompatActivity {
ActivitySplashscreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Thread thread=new Thread(){
            public void run(){
                try {
                    sleep(3000);
                }
                catch (Exception e){
                    e.printStackTrace();

                }finally {
                    Intent intent=new Intent(Splashscreen_Activity.this,PhoneNumberActivity.class);
                    startActivity(intent);
                }
            }
        };thread.start();

    }
}