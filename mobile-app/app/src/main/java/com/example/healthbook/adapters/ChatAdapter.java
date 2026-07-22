package com.example.healthbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.data.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(message.getTimestamp()));

        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            // Sent message
            holder.layoutSent.setVisibility(View.VISIBLE);
            holder.layoutReceived.setVisibility(View.GONE);
            holder.tvSentMsg.setText(message.getText());
        } else {
            // Received message
            holder.layoutSent.setVisibility(View.GONE);
            holder.layoutReceived.setVisibility(View.VISIBLE);
            holder.tvReceivedMsg.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutSent, layoutReceived;
        TextView tvSentMsg, tvReceivedMsg;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSent = itemView.findViewById(R.id.layoutSent);
            layoutReceived = itemView.findViewById(R.id.layoutReceived);
            tvSentMsg = itemView.findViewById(R.id.tvSentMsg);
            tvReceivedMsg = itemView.findViewById(R.id.tvReceivedMsg);
        }
    }
}
