package com.example.healthbook.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.ChatAdapter;
import com.example.healthbook.data.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvChatTitle;
    private ImageView btnBack;

    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private String currentUserId;
    private String otherUserId;
    private String otherUserName;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvChatTitle = findViewById(R.id.tvChatTitle);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        
        otherUserId = getIntent().getStringExtra("OTHER_USER_ID");
        otherUserName = getIntent().getStringExtra("OTHER_USER_NAME");

        if (otherUserName != null) {
            tvChatTitle.setText(otherUserName);
        }

        btnBack.setOnClickListener(v -> finish());

        // Generate Chat ID (alphabetical order to ensure both users have same ID)
        if (currentUserId.compareTo(otherUserId) < 0) {
            chatId = currentUserId + "_" + otherUserId;
        } else {
            chatId = otherUserId + "_" + currentUserId;
        }

        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messageList, currentUserId);
        rvChatMessages.setAdapter(chatAdapter);

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        CollectionReference messagesRef = db.collection("Chats").document(chatId).collection("Messages");
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi tải tin nhắn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (value != null) {
                messageList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Message message = doc.toObject(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvChatMessages.scrollToPosition(messageList.size() - 1);
                }
            }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        long timestamp = System.currentTimeMillis();
        Message message = new Message(text, currentUserId, timestamp);

        db.collection("Chats").document(chatId).collection("Messages").add(message).addOnSuccessListener(documentReference -> {
            etMessage.setText("");
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
        });
        
        // Cập nhật last message info
        db.collection("Chats").document(chatId).set(
                new java.util.HashMap<String, Object>() {{
                    put("lastMessage", text);
                    put("timestamp", timestamp);
                    put("user1", currentUserId);
                    put("user2", otherUserId);
                }}
        );
    }
}
