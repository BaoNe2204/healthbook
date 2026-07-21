package com.example.healthbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthbook.R;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Item item);
    }

    public static class Item {
        public String id;
        public String title;
        public String sub;

        public Item(String id, String title, String sub) {
            this.id = id;
            this.title = title;
            this.sub = sub;
        }
    }

    private List<Item> list;
    private OnDeleteClickListener listener;

    public AdminCategoryAdapter(List<Item> list, OnDeleteClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = list.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvSub.setText(item.sub != null ? item.sub : "");

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvSub = itemView.findViewById(R.id.tvItemSub);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
