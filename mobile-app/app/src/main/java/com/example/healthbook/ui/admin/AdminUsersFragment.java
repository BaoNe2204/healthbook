package com.example.healthbook.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;
import com.example.healthbook.adapters.AdminUserAdapter;
import com.example.healthbook.data.models.UserProfile;
import com.example.healthbook.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private EditText etSearchUser;
    private AdminUserAdapter adapter;
    private List<UserProfile> usersList = new ArrayList<>();
    private List<UserProfile> filteredList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        rvUsers = view.findViewById(R.id.rvAdminUsers);
        etSearchUser = view.findViewById(R.id.etSearchUser);
        
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new AdminUserAdapter(filteredList);
        rvUsers.setAdapter(adapter);

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadUsers();

        return view;
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(usersList);
        } else {
            text = text.toLowerCase();
            for (UserProfile user : usersList) {
                boolean matchName = user.getDisplayName() != null && user.getDisplayName().toLowerCase().contains(text);
                boolean matchEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(text);
                boolean matchRole = user.getRole() != null && user.getRole().toLowerCase().contains(text);
                
                if (matchName || matchEmail || matchRole) {
                    filteredList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadUsers() {
        RetrofitClient.getInstance().getApiService().getAdminUsers().enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersList.clear();
                    usersList.addAll(response.body());
                    filter(etSearchUser.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}