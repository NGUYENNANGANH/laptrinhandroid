package com.example.truyenchu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Thêm import cho Toast

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenchu.R;
import com.example.truyenchu.model.Chuong;
import java.util.List;

public class ChuongAdapter extends RecyclerView.Adapter<ChuongAdapter.ViewHolder> {
    private final List<Chuong> chuongList;
    private Context context;

    public ChuongAdapter(Context context, List<Chuong> chuongList) {
        this.context = context;
        this.chuongList = chuongList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chuong, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chuong chuong = chuongList.get(position);
        holder.tvTenChuong.setText(chuong.getTen());
        holder.tvNgayDang.setText(chuong.getNgayDang());

        // Gán sự kiện click cho nút tải xuống
        holder.btnDownload.setOnClickListener(v -> {
            // Hiển thị thông báo tạm thời để xác nhận nút hoạt động
            Toast.makeText(context, "Bắt đầu tải " + chuong.getTen(), Toast.LENGTH_SHORT).show();

            // Trong tương lai, bạn có thể thêm logic tải truyện về máy tại đây
            // ví dụ: gọi một DownloadManager hoặc lưu nội dung chương vào database cục bộ.
        });
    }

    @Override
    public int getItemCount() {
        return chuongList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenChuong, tvNgayDang;
        ImageButton btnDownload; // Nút tải xuống

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenChuong = itemView.findViewById(R.id.tv_ten_chuong);
            tvNgayDang = itemView.findViewById(R.id.tv_ngay_dang);
            // Ánh xạ nút bấm từ layout bằng ID
            btnDownload = itemView.findViewById(R.id.btn_download_chapter);
        }
    }
}
