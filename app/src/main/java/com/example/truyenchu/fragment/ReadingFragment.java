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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenchu.R;
import com.example.truyenchu.adapter.ListChapterAdapter;
import com.example.truyenchu.model.Chuong;
import com.example.truyenchu.model.Truyen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadingFragment extends Fragment {

    private static final String TAG = "ReadingFragment";
    public static final String KEY_STORY_ID = "storyId";

    private TextView tvStoryContent;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private String storyId;
    private RecyclerView rvChapters;
    private ListChapterAdapter chapterAdapter;
    private final List<Chuong> chapterList = new ArrayList<>();
    private Truyen currentTruyen; // Lưu thông tin truyện hiện tại
    private boolean isFirstChapterLoaded = false; // Cờ để chỉ tải chương đầu một lần


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

        // Ánh xạ views
        drawerLayout = view.findViewById(R.id.drawer_layout);
        tvStoryContent = view.findViewById(R.id.tv_story_content);
        toolbar = view.findViewById(R.id.toolbar);
        rvChapters = view.findViewById(R.id.rv_chapters);

        // Cài đặt
        setupRecyclerView();
        setupEventListeners(view);

        // Tải dữ liệu
        if (storyId != null) {
            loadStoryInfo(storyId); // Tải thông tin truyện
            loadChapterList(storyId); // Tải danh sách chương
        }
    }

    private void setupRecyclerView() {
        rvChapters.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo Adapter với danh sách rỗng, và thêm listener để xử lý click
//        chapterAdapter = new ListChapterAdapter(chapterList, chapter -> {
//            loadChapterContent(chapter); // Khi click vào một chương, tải nội dung của nó
//            drawerLayout.closeDrawer(GravityCompat.END); // Đóng drawer
//        });

        chapterAdapter = new ListChapterAdapter(chapterList, new ListChapterAdapter.OnChapterClickListener() {
            @Override
            public void onChapterClick(Chuong chapter) {
                loadChapterContent(chapter);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        rvChapters.setAdapter(chapterAdapter);
    }

    private void setupEventListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_report_error) {
                new ReportErrorDialogFragment().show(getParentFragmentManager(), "ReportErrorDialog");
                return true;
            }
            return false;
        });
        view.findViewById(R.id.btn_chapters).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            new SettingsBottomSheetFragment().show(getParentFragmentManager(), "SettingsBottomSheet");
        });
    }

    /**
     * Chỉ tải thông tin của truyện (tên, tác giả,...) để hiển thị trên toolbar
     */
    private void loadStoryInfo(String storyId) {
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("truyen").child(storyId);
        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentTruyen = snapshot.getValue(Truyen.class);
                if (currentTruyen != null) {
                    toolbar.setSubtitle(currentTruyen.getTen()); // Set tên truyện làm subtitle
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load story info.", error.toException());
            }
        });
    }


    /**
     * Hàm này thực hiện tải danh sách các chương của một truyện từ Firebase.
     * Cấu trúc trên Firebase được truy vấn là: chuong -> storyId -> [danh sách chương]
     * @param storyId ID của truyện cần tải chương (vd: "truyen_01").
     */
    private void loadChapterList(String storyId) {
        // SỬA ĐỔI: Thay đổi đường dẫn để trỏ đến node "chuong" theo cấu trúc của bạn
        DatabaseReference chaptersRef = FirebaseDatabase.getInstance()
                .getReference("chuong") // Trỏ tới node gốc "chuong"
                .child(storyId);       // Rồi đến ID của truyện (vd: "truyen_01")

        chaptersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterList.clear(); // Xóa danh sách cũ
                for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                    Chuong chuong = chapterSnapshot.getValue(Chuong.class);
                    if (chuong != null) {
                        chapterList.add(chuong);
                    }
                }
                // (Tùy chọn) Sắp xếp chương theo ID hoặc tên nếu cần
                // Collections.sort(chapterList, (c1, c2) -> c1.getId().compareTo(c2.getId()));

                chapterAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView

                // Tự động tải nội dung của chương đầu tiên nếu chưa tải
                if (!isFirstChapterLoaded && !chapterList.isEmpty()) {
                    loadChapterContent(chapterList.get(0)); // Lấy chương đầu tiên trong danh sách
                    isFirstChapterLoaded = true;
                }
                Log.i(TAG, "Loaded " + chapterList.size() + " chapters.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load chapter list.", databaseError.toException());
            }
        });
    }

    /**
     * Tải và hiển thị nội dung của một chương cụ thể
     * @param chapter Đối tượng Chuong cần hiển thị
     */
    private void loadChapterContent(Chuong chapter) {
        if (chapter == null) return;

        // Hiển thị nội dung
        // Sử dụng replace để thay thế "\n" trong database thành ký tự xuống dòng thật
        tvStoryContent.setText(chapter.getNoiDung().replace("\\n", "\n"));

        // Cập nhật tiêu đề trên Toolbar
        toolbar.setTitle(chapter.getTen());

        // Cuộn lên đầu trang mỗi khi chuyển chương
        View nestedScrollView = getView().findViewById(R.id.nested_scroll_view_reading);
        if (nestedScrollView != null) {
            nestedScrollView.scrollTo(0, 0);
        }
    }
}