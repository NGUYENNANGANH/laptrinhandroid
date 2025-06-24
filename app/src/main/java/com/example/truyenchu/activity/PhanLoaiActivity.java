package com.example.truyenchu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenchu.R;
import com.example.truyenchu.adapter.TruyenTrendingAdapter;
import com.example.truyenchu.model.TheLoai;
import com.example.truyenchu.model.Truyen;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PhanLoaiActivity extends AppCompatActivity {

    private static final String TAG = "PhanLoaiActivity";

    private RecyclerView rvTruyen;
    private TruyenTrendingAdapter truyenTrendingAdapter;
    private List<Truyen> displayedTruyenList = new ArrayList<>(); // Danh sách hiển thị
    private List<Truyen> allTruyenList = new ArrayList<>(); // Danh sách chứa tất cả truyện
    private EditText etTimKiem;
    private DatabaseReference database;
    private TabLayout tabLayout;
    private TextView tvListTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phan_loai);

        database = FirebaseDatabase.getInstance().getReference();

        etTimKiem = findViewById(R.id.et_tim_kiem);
        tabLayout = findViewById(R.id.tab_layout_the_loai);
        tvListTitle = findViewById(R.id.tv_list_title);

        etTimKiem.setOnClickListener(v -> startActivity(new Intent(PhanLoaiActivity.this, TimKiemActivity.class)));

        setupRecyclerView();
        loadAllTruyenData(); // Tải tất cả truyện trước
        setupTabs();
    }

    private void setupRecyclerView() {
        rvTruyen = findViewById(R.id.rv_truyen);
        rvTruyen.setLayoutManager(new LinearLayoutManager(this));
        rvTruyen.setNestedScrollingEnabled(false);
        truyenTrendingAdapter = new TruyenTrendingAdapter(this, displayedTruyenList, truyen -> {
            Intent intent = new Intent(PhanLoaiActivity.this, ChiTietTruyenActivity.class);
            intent.putExtra("TRUYEN_ID", truyen.getId());
            startActivity(intent);
        });
        rvTruyen.setAdapter(truyenTrendingAdapter);
    }

    private void setupTabs() {
        database.child("the_loai").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Thêm tab "Tất cả" vào đầu tiên
                    tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TheLoai theLoai = dataSnapshot.getValue(TheLoai.class);
                        if (theLoai != null) {
                            tabLayout.addTab(tabLayout.newTab().setText(theLoai.getTen()));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tải thể loại: " + error.getMessage());
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedCategory = tab.getText().toString();
                filterAndDisplayTruyen(selectedCategory);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAllTruyenData() {
        database.child("truyen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTruyenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Truyen truyen = snapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(snapshot.getKey());
                        allTruyenList.add(truyen);
                    }
                }
                // Sau khi tải xong, hiển thị danh sách "Tất cả" ban đầu
                filterAndDisplayTruyen("Tất cả");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tải truyện: " + error.getMessage());
            }
        });
    }

    private void filterAndDisplayTruyen(String category) {
        displayedTruyenList.clear();
        List<Truyen> filteredList = new ArrayList<>();

        if (category.equalsIgnoreCase("Tất cả")) {
            tvListTitle.setText("Top Trending This Week");
            // Sắp xếp theo đánh giá để lấy top trending
            Collections.sort(allTruyenList, (t1, t2) -> Double.compare(t2.getDanhGia(), t1.getDanhGia()));
            filteredList.addAll(allTruyenList);
        } else {
            tvListTitle.setText("Top Trending " + category);
            for (Truyen truyen : allTruyenList) {
                // Kiểm tra xem tag thể loại của truyện có chứa tên thể loại được chọn không
                if (truyen.getTheLoaiTags() != null && truyen.getTheLoaiTags().toLowerCase().contains(category.toLowerCase())) {
                    filteredList.add(truyen);
                }
            }
            // Sắp xếp danh sách đã lọc theo đánh giá
            Collections.sort(filteredList, (t1, t2) -> Double.compare(t2.getDanhGia(), t1.getDanhGia()));
        }

        // Lấy 5 truyện đầu tiên sau khi đã lọc và sắp xếp
        int limit = Math.min(filteredList.size(), 5);
        for (int i = 0; i < limit; i++) {
            displayedTruyenList.add(filteredList.get(i));
        }

        truyenTrendingAdapter.notifyDataSetChanged();
        Log.d(TAG, "Đã lọc cho thể loại '" + category + "', hiển thị " + displayedTruyenList.size() + " truyện.");
    }
}
