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

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private final Context context;
    private List<Truyen> truyenList;
    private OnItemClickListener listener; // << THÊM DÒNG NÀY

    // << THÊM INTERFACE NÀY
    public interface OnItemClickListener {
        void onItemClick(Truyen truyen);
    }

    // << THÊM PHƯƠNG THỨC NÀY
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LibraryAdapter(Context context, List<Truyen> truyenList) {
        this.context = context;
        this.truyenList = truyenList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout mới item_library_truyen.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_library_truyen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);

        // Gán dữ liệu vào các view
        holder.title.setText(truyen.getTen());
        Glide.with(context)
                .load(truyen.getAnhBia())
                .placeholder(R.drawable.default_avatar) // Ảnh chờ
                .error(R.drawable.default_avatar) // Ảnh lỗi
                .into(holder.cover);

        // TODO: Thêm logic của bạn để quyết định khi nào hiển thị nhãn "NEW CHAPTER"
        // Ví dụ: dựa trên một trường boolean trong model Truyen của bạn
        // if (truyen.isHasNewChapter()) {
        //     holder.newChapterBadge.setVisibility(View.VISIBLE);
        // } else {
        //     holder.newChapterBadge.setVisibility(View.GONE);
        // }

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

    // ViewHolder đã được cập nhật để dùng ID mới
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView newChapterBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view bằng ID MỚI
            cover = itemView.findViewById(R.id.iv_anh_bia_featured);
            title = itemView.findViewById(R.id.tv_ten_truyen_featured);
            newChapterBadge = itemView.findViewById(R.id.tv_new_chapter_badge);
        }
    }
}
