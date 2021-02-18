package com.example.chattingapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chattingapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumberActivity extends AppCompatActivity {
ActivityPhoneNumberBinding binding;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Create Account");

        auth=FirebaseAuth.getInstance();

        if (auth.getCurrentUser() !=null)
        {
            startActivity(new Intent(PhoneNumberActivity.this,MainActivity.class));
            finish();
        }


        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhoneNumberActivity.this,OtpActivity.class);
                intent.putExtra("phonenumber",binding.phonebox.getText().toString());
                startActivity(intent);

            }
        });
    }
}