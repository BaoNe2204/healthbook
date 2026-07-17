package com.example.healthbook.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthbook.R;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        ImageView btnSend = view.findViewById(R.id.btnSend);
        TextView tvChatTitle = view.findViewById(R.id.tvChatTitle);

        if (getArguments() != null && getArguments().getString("doctorName") != null) {
            tvChatTitle.setText(getArguments().getString("doctorName"));
        }

        // Dummy initial message
        messages.add(new ChatMessage("Chào bạn, mình có thể giúp gì cho bạn?", true));

        adapter = new ChatAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                messages.add(new ChatMessage(text, false));
                adapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
                etMessage.setText("");
                
                // Simulate reply
                rvMessages.postDelayed(() -> {
                    messages.add(new ChatMessage("Bác sĩ hiện đang bận, xin vui lòng để lại lời nhắn.", true));
                    adapter.notifyItemInserted(messages.size() - 1);
                    rvMessages.scrollToPosition(messages.size() - 1);
                }, 1000);
            }
        });

        return view;
    }

    // Inner classes for simple chat
    private static class ChatMessage {
        String text;
        boolean isReceived;

        ChatMessage(String text, boolean isReceived) {
            this.text = text;
            this.isReceived = isReceived;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        List<ChatMessage> list;

        ChatAdapter(List<ChatMessage> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage msg = list.get(position);
            if (msg.isReceived) {
                holder.layoutReceived.setVisibility(View.VISIBLE);
                holder.layoutSent.setVisibility(View.GONE);
                holder.tvReceivedMsg.setText(msg.text);
            } else {
                holder.layoutReceived.setVisibility(View.GONE);
                holder.layoutSent.setVisibility(View.VISIBLE);
                holder.tvSentMsg.setText(msg.text);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            View layoutReceived, layoutSent;
            TextView tvReceivedMsg, tvSentMsg;

            ViewHolder(View v) {
                super(v);
                layoutReceived = v.findViewById(R.id.layoutReceived);
                layoutSent = v.findViewById(R.id.layoutSent);
                tvReceivedMsg = v.findViewById(R.id.tvReceivedMsg);
                tvSentMsg = v.findViewById(R.id.tvSentMsg);
            }
        }
    }
}
