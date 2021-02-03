package com.example.chit2chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.chit2chat.Adapter.FragmentsAdapter;
import com.example.chit2chat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
      getSupportActionBar().setTitle(Html.fromHtml("<font color=\"Black\">" + getString(R.string.app_name) + "</font>"));


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPagers.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tablayouts.setupWithViewPager(binding.viewPagers);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent sett=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(sett);
                break;
            case R.id.logout:
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.groupchats:
                Intent intentt=new Intent(MainActivity.this,GroupChatActivity.class);
                startActivity(intentt);
                break;
        }
        return true;
    }
}