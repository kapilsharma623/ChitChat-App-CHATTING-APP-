package com.example.chattingapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chattingapp.Models.User;
import com.example.chattingapp.databinding.ActivitySetupProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedimage;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Profile Setup");
        dialog = new ProgressDialog(this);
        dialog.setMessage("Wait,Logging In to your Account");
        dialog.setCancelable(false);



        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });
        binding.continuebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
                String name=binding.namebox.getText().toString();
                if (name.isEmpty())
                {
                    binding.namebox.setError("Please type your name");
                }
                if (selectedimage!=null)
                {
                    StorageReference reference= storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                       if (task.isSuccessful())
                       {
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   String imageurl=uri.toString();
                                   String uid=auth.getUid();
                                   String phone=auth.getCurrentUser().getPhoneNumber();
                                   String name=binding.namebox.getText().toString();
                                   String statuss="";
                                   User user=new User(uid,name,phone,imageurl,statuss);
                                   database.getReference().child("Users").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           dialog.dismiss();
                                           Intent intent=new Intent(SetupProfileActivity.this,MainActivity.class);
                                           startActivity(intent);
                                           finish();
                                       }
                                   });
                               }
                           });
                       }
                       else
                       {
                           String uid=auth.getUid();
                           String phone=auth.getCurrentUser().getPhoneNumber();
                           String name=binding.namebox.getText().toString();
                           String statusss="";
                           User user=new User(uid,name,phone,"No Image",statusss);
                           database.getReference().child("Users").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   dialog.dismiss();
                                   Intent intent=new Intent(SetupProfileActivity.this,MainActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                           });
                       }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null)
        {
            if (data.getData() != null)
            {
                binding.imageView.setImageURI(data.getData() );
                selectedimage=data.getData();
            }
        }
    }
}