package com.example.truyenchu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenchu.model.Chuong;
import com.example.truyenchu.R;
import java.util.List;

public class ListChapterAdapter extends RecyclerView.Adapter<ListChapterAdapter.ViewHolder> {
    private List<Chuong> chapterList;
    // Thêm listener để xử lý khi người dùng nhấn vào một chương
    public ListChapterAdapter(List<Chuong> chapterList) { this.chapterList = chapterList; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chuong chapter = chapterList.get(position);
        holder.title.setText(chapter.getTen());
    }
    @Override public int getItemCount() { return chapterList.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_chapter_item_title);
        }
    }
}