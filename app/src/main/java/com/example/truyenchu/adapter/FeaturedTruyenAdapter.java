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

public class FeaturedTruyenAdapter extends RecyclerView.Adapter<FeaturedTruyenAdapter.ViewHolder> {

    private final Context context;
    private final List<Truyen> truyenList;

    private OnItemClickListener listener; // THÊM MỚI

    // THÊM MỚI: Interface để xử lý click
    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
    }

    // THÊM MỚI: Method để fragment có thể set listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public FeaturedTruyenAdapter(Context context, List<Truyen> truyenList) {
        this.context = context;
        this.truyenList = truyenList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_truyen_featured, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);
        holder.tvTenTruyen.setText(truyen.getTen());
//        holder.tvTacGia.setText(truyen.getTacGia());
        // TODO: Cập nhật thông tin chương mới nhất và thời gian từ dữ liệu thực tế
        // Ví dụ: holder.tvUpdateInfo.setText("Chapter " + truyen.getLatestChapter());
//        holder.tvUpdateInfo.setText("Cập nhật gần đây");


        Glide.with(context)
                .load(truyen.getAnhBia())
                .placeholder(R.drawable.hero_background)
                .error(R.drawable.hero_background)
                .into(holder.ivAnhBia);

        // Bắt sự kiện click cho cả item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(truyen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return truyenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAnhBia;
        TextView tvTenTruyen;
//        TextView tvTacGia;
//        TextView tvUpdateInfo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAnhBia = itemView.findViewById(R.id.iv_anh_bia_featured);
            tvTenTruyen = itemView.findViewById(R.id.tv_ten_truyen_featured);
//            tvTacGia = itemView.findViewById(R.id.tv_tac_gia_recent);
//            tvUpdateInfo = itemView.findViewById(R.id.tv_update_info_recent);
        }
    }
}