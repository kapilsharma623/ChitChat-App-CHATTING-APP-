package com.example.chattingapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chattingapp.Adapters.TopStatusAdapter;
import com.example.chattingapp.Adapters.UsersAdapter;
import com.example.chattingapp.Models.Status;
import com.example.chattingapp.Models.User;
import com.example.chattingapp.Models.UserStatus;
import com.example.chattingapp.R;
import com.example.chattingapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;
    Long date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("ChitChat");
        dialog=new ProgressDialog(this);
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses=new ArrayList<>();

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      user=snapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        usersAdapter = new UsersAdapter(this, users);
        binding.recyclerview.setAdapter(usersAdapter);
        statusAdapter=new TopStatusAdapter(this,userStatuses);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statuslist.setLayoutManager(layoutManager);
        binding.statuslist.setAdapter(statusAdapter);
        binding.recyclerview.setAdapter(usersAdapter);

        binding.recyclerview.showShimmerAdapter();
        binding.statuslist.showShimmerAdapter();

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    User user=snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        users.add(user);
                    }

                }
                binding.recyclerview.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           if (snapshot.exists())
           {
               userStatuses.clear();
               for (DataSnapshot storysnapshot: snapshot.getChildren())
               {
                   UserStatus status =new UserStatus();
                   status.setName(storysnapshot.child("name").getValue(String.class));
                   status.setProfileimage(storysnapshot.child("profileimage").getValue(String.class));
                   status.setLastUpdated(storysnapshot.child("lastUpdated").getValue(Long.class));

                   ArrayList<Status> statuses=new ArrayList<>();
                   for (DataSnapshot statussnapshot:storysnapshot.child("statuses").getChildren())
                   {
                       Status samplestatus=statussnapshot.getValue(Status.class);
                       statuses.add(samplestatus);
                   }
                   status.setStatuses(statuses);
                   userStatuses.add(status);
               }
               binding.statuslist.hideShimmerAdapter();
               statusAdapter.notifyDataSetChanged();
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.status:
                        Intent intent =new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData()!=null)
        {
            dialog.show();
            FirebaseStorage storage=FirebaseStorage.getInstance();
            Date date=new Date();
            StorageReference reference=storage.getReference().child("status").child(date.getTime()+"");
            reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if (task.isSuccessful())
               {
                   reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           UserStatus userStatus=new UserStatus();
                           userStatus.setName(user.getName());
                           userStatus.setProfileimage(user.getProfileimage());
                           userStatus.setLastUpdated(date.getTime());

                           HashMap<String,Object> obj=new HashMap<>();
                           obj.put("name",userStatus.getName());
                           obj.put("profileimage",userStatus.getProfileimage());
                           obj.put("lastUpdated",userStatus.getLastUpdated());

                           String imageUrl=uri.toString();
                           Status status=new Status(imageUrl,userStatus.getLastUpdated());

                           database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                   .updateChildren(obj);
                           database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid())
                                   .child("statuses").push().setValue(status);

                           dialog.dismiss();
                       }
                   });
               }
                }
            });
        }
    }
    private void onShareClicked() {

        String link = "https://github.com/kapilsharma623/ChitChat-App-CHATTING-APP-";
        String appname="ChitChat App";

        Uri uri = Uri.parse(link);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link.toString());
        intent.putExtra(Intent.EXTRA_TITLE,appname);

        startActivity(Intent.createChooser(intent, "Share Link"));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchicon:
                Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this,Settings_Activity.class));
                break;
            case R.id.invite:
                onShareClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}