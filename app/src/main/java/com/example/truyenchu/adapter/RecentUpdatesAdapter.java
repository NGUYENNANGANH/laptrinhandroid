package com.example.truyenchu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenchu.R;
import com.example.truyenchu.model.Truyen;

import java.util.List;

public class RecentUpdatesAdapter extends RecyclerView.Adapter<RecentUpdatesAdapter.ViewHolder> {

    private final Context context;
    private final List<Truyen> truyenList;

    public RecentUpdatesAdapter(Context context, List<Truyen> truyenList) {
        this.context = context;
        this.truyenList = truyenList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // SỬA Ở ĐÂY: Dùng đúng layout item_truyen_recent
        View view = LayoutInflater.from(context).inflate(R.layout.item_truyen_recent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);
        holder.tvTenTruyen.setText(truyen.getTen());
        holder.tvTacGia.setText(truyen.getTacGia());

        // TODO: Cập nhật thông tin chương và thời gian thực tế
        holder.tvUpdateInfo.setText("Cập nhật gần đây");

        Glide.with(context)
                .load(truyen.getAnhBia())
                .placeholder(R.drawable.hero_background)
                .error(R.drawable.hero_background)
                .into(holder.ivAnhBia);
    }

    @Override
    public int getItemCount() {
        return truyenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAnhBia;
        TextView tvTenTruyen;
        TextView tvTacGia;
        TextView tvUpdateInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // SỬA Ở ĐÂY: Ánh xạ đúng các ID từ item_truyen_recent.xml
            ivAnhBia = itemView.findViewById(R.id.iv_anh_bia_recent);
            tvTenTruyen = itemView.findViewById(R.id.tv_ten_truyen_recent);
            tvTacGia = itemView.findViewById(R.id.tv_tac_gia_recent);
            tvUpdateInfo = itemView.findViewById(R.id.tv_update_info_recent);
        }
    }
}