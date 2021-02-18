package com.example.chattingapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.chattingapp.Activities.ChatActivity;
import com.example.chattingapp.Models.User;
import com.example.chattingapp.R;
import com.example.chattingapp.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        String senderid = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderid + user.getUid();

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastmsg = snapshot.child("lastMsg").getValue(String.class);
                    Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                    holder.binding.lastmessage.setText(lastmsg);
                    //epoch to human time
                    Date date = new Date(time);
                    DateFormat format = new SimpleDateFormat("hh.mm aa");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formatted = format.format(date);
                    format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));//your zone
                    formatted = format.format(date);


                    holder.binding.msgtime.setText(formatted);
                    holder.binding.msgtime.setTextColor(Color.BLACK);

                } else {
                    holder.binding.lastmessage.setText("Tap to Chat");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.binding.username.setText(user.getName());
        Glide.with(context).load(user.getProfileimage()).placeholder(R.drawable.user).into(holder.binding.profileimg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        RowConversationBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }

}
