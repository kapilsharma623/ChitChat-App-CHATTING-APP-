package com.example.chit2chat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chit2chat.ChatDetailActivity;
import com.example.chit2chat.Models.MessagesModel;
import com.example.chit2chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessagesModel> messagesModels;
    Context context;
    String recid;


    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recid) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recid = recid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messagesModel=messagesModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure,you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database=FirebaseDatabase.getInstance();
                                String senderroom=FirebaseAuth.getInstance().getUid() + recid;
                                database.getReference().child("chats").child(senderroom)
                                        .child(messagesModel.getMessageid()).setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

                return false;
            }
        });
        if(holder.getClass()==SenderViewHolder.class)
        {
            ((SenderViewHolder)holder).sendertext.setText(messagesModel.getMessage());

        }
        else {
            ((ReceiverViewHolder)holder).receivermsg.setText(messagesModel.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receivermsg,receivertime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receivermsg=itemView.findViewById(R.id.receivertext);
            receivertime=itemView.findViewById(R.id.receivertime);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView sendertext,sendertime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendertext=itemView.findViewById(R.id.sendertext);
            sendertime=itemView.findViewById(R.id.sendertime);

        }
    }
}
