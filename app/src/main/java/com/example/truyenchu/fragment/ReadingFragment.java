package com.example.truyenchu.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.truyenchu.R;
import com.example.truyenchu.model.Truyen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReadingFragment extends Fragment {

    private static final String TAG = "ReadingFragment";

    //Định nghĩa một "key" hằng số để tránh gõ sai
    public static final String KEY_STORY_ID = "storyId";

    private TextView tvStoryContent;
    private Toolbar toolbar;
    private TextView tvChapterTitle;
    private DrawerLayout drawerLayout;
    private String storyId;

    //Nhận dữ liệu trong onCreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storyId = getArguments().getString(KEY_STORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view từ layout
        drawerLayout = view.findViewById(R.id.drawer_layout);
        tvStoryContent = view.findViewById(R.id.tv_story_content);
        toolbar = view.findViewById(R.id.toolbar);
        tvChapterTitle = view.findViewById(R.id.tv_chapter_title);


        // Bắt sự kiện cho các nút
        view.findViewById(R.id.btn_chapters).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        // TODO: Thêm sự kiện cho các nút khác như settings, back...

        // Nếu có storyId, bắt đầu tải dữ liệu
        if (storyId != null) {
            loadStoryData(storyId);
            // TODO: Tải danh sách chương và nội dung chương đầu tiên
        }
    }

    private void loadStoryData(String storyId) {
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("truyen").child(storyId);
        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Truyen truyen = snapshot.getValue(Truyen.class);
                if (truyen != null) {
                    // Hiển thị tên truyện lên Toolbar
                    // Bạn nên tạo một TextView riêng cho tên truyện thay vì dùng title của Toolbar
                    // để không bị giới hạn về độ dài và style.
                    // Ở đây, ta sẽ hiển thị tạm lên TextView có sẵn
                    tvChapterTitle.setText(truyen.getTen());

                    // TODO: Hiển thị nội dung truyện (của chương đầu tiên)
                    // Bạn cần một truy vấn khác để lấy nội dung chương
                    tvStoryContent.setText(truyen.getMoTa()); // Tạm thời hiển thị mô tả
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load story data.", error.toException());
            }
        });
    }
}