package com.example.chattingapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.chattingapp.Adapters.MessagesAdapter;
import com.example.chattingapp.Models.Message;
import com.example.chattingapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);

        String name = getIntent().getStringExtra("name");
        String receiveruid = getIntent().getStringExtra("uid");
        String senderuid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderuid + receiveruid;
        receiverRoom = receiveruid + senderuid;
        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Message message = snapshot1.getValue(Message.class);
                    message.setMessageid(snapshot1.getKey());
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intentt=new Intent();
                    intentt.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intentt);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messagetxt = binding.messagebox.getText().toString();
                if (messagetxt.isEmpty() || messagetxt == "") {
                    binding.messagebox.setError("Type your message");
                } else {
                    Date date = new Date();
                    Message message = new Message(messagetxt, senderuid, date.getTime());
                    binding.messagebox.setText("");

                    String randomkey = database.getReference().push().getKey();

                    HashMap<String, Object> lastmsgobj = new HashMap<>();
                    lastmsgobj.put("lastMsg", message.getMessage());
                    lastmsgobj.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastmsgobj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastmsgobj);

                    database.getReference().child("chats").child(senderRoom).child("messages").child(randomkey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats").child(receiverRoom).child("messages").child(randomkey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                        }
                    });

                }


            }
        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}