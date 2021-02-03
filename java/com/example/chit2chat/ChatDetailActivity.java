package com.example.chit2chat;


import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chit2chat.Adapter.ChatAdapter;
import com.example.chit2chat.Models.MessagesModel;
import com.example.chit2chat.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    int count=0;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        binding.backbtnid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent switchs=new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(switchs);
            }
        });

        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();

        final String senderId=mAuth.getUid();
        String receiverId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        binding.usernameid.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.man).into(binding.profileImage);

        final ArrayList<MessagesModel> messagesModels=new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messagesModels,this,receiverId);
        binding.chatrecyclerview.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        final  String senderroom=senderId+receiverId;
        final  String receiverroom=receiverId+senderId;

        binding.chatrecyclerview.setLayoutManager(layoutManager);

        database.getReference().child("chats").child(senderroom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModels.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    MessagesModel model=snapshot1.getValue(MessagesModel.class);
                    model.setMessageid(snapshot1.getKey());
                    messagesModels.add(model);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendtbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.msgtext.getText().toString().isEmpty())
                {
                    binding.msgtext.setError("Enter Message");
                    return;
                }
                String message=binding.msgtext.getText().toString();
                final  MessagesModel model=new MessagesModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                binding.msgtext.setText("");

                database.getReference().child("chats").child(senderroom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats").child(receiverroom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
    }
}