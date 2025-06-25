package com.example.truyenchu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button; // THÊM MỚI
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // THÊM MỚI

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat; // THÊM MỚI
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenchu.R;
import com.example.truyenchu.adapter.BinhLuanAdapter;
import com.example.truyenchu.adapter.ChuongAdapter;
import com.example.truyenchu.model.BinhLuan;
import com.example.truyenchu.model.Chuong;
import com.example.truyenchu.model.Truyen;
import com.google.firebase.auth.FirebaseAuth; // THÊM MỚI
import com.google.firebase.auth.FirebaseUser; // THÊM MỚI
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChiTietTruyenActivity extends AppCompatActivity {

    private ImageView ivAnhBia;
    private TextView tvTenTruyen, tvTacGia, tvTrangThai, tvDanhGia, tvMoTa, tvTheLoai;
    private RecyclerView rvChuong, rvBinhLuan;
    private ChuongAdapter chuongAdapter;
    private BinhLuanAdapter binhLuanAdapter;
    private List<Chuong> chuongList = new ArrayList<>();
    private List<BinhLuan> binhLuanList = new ArrayList<>();
    private String truyenId;
    private DatabaseReference database;

    // --- THÊM CÁC BIẾN MỚI ---
    private Button btnFollow;
    private FirebaseAuth mAuth;
    private boolean isTruyenInLibrary = false; // Biến cờ để kiểm tra trạng thái


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_truyen);

        truyenId = getIntent().getStringExtra("TRUYEN_ID");
        if (truyenId == null || truyenId.isEmpty()) {
            finish();
            return;
        }
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance(); // THÊM MỚI: Khởi tạo FirebaseAuth

        initViews();
        setupToolbar();
        setupRecyclerViews();
        loadAllData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_chi_tiet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        ivAnhBia = findViewById(R.id.iv_anh_bia_detail);
        tvTenTruyen = findViewById(R.id.tv_ten_truyen_detail);
        tvTacGia = findViewById(R.id.tv_tac_gia_detail);
        tvTheLoai = findViewById(R.id.tv_the_loai_detail);
        tvTrangThai = findViewById(R.id.tv_trang_thai_detail);
        tvDanhGia = findViewById(R.id.tv_danh_gia_detail);
        tvMoTa = findViewById(R.id.tv_mo_ta_detail);
        rvChuong = findViewById(R.id.rv_chuong);
        rvBinhLuan = findViewById(R.id.rv_binh_luan);
        btnFollow = findViewById(R.id.btn_follow); // THÊM MỚI: Ánh xạ nút follow
    }

    private void setupRecyclerViews() {
        // ... code không đổi ...
        rvChuong.setLayoutManager(new LinearLayoutManager(this));
        rvChuong.setNestedScrollingEnabled(false);
        chuongAdapter = new ChuongAdapter(this, chuongList);
        rvChuong.setAdapter(chuongAdapter);

        rvBinhLuan.setLayoutManager(new LinearLayoutManager(this));
        rvBinhLuan.setNestedScrollingEnabled(false);
        binhLuanAdapter = new BinhLuanAdapter(binhLuanList);
        rvBinhLuan.setAdapter(binhLuanAdapter);
    }

    private void loadAllData() {
        loadTruyenInfo();
        loadChuongList();
        loadBinhLuanList();
        setupFollowButton(); // CẬP NHẬT: Gọi hàm cài đặt cho nút follow
    }

    // ... các hàm loadChuongList và loadBinhLuanList không đổi ...

    private void loadTruyenInfo() {
        // ... code không đổi ...
        database.child("truyen").child(truyenId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Truyen truyen = snapshot.getValue(Truyen.class);
                if (truyen != null) {
                    tvTenTruyen.setText(truyen.getTen());
                    tvTacGia.setText(truyen.getTacGia());
                    tvTheLoai.setText(truyen.getTheLoaiTags());
                    tvTrangThai.setText(truyen.getTrangThai());
                    tvMoTa.setText(truyen.getMoTa());
                    String danhGiaText = String.format(Locale.US, "%.1f (%d reviews)", truyen.getDanhGia(), truyen.getSoLuongDanhGia());
                    tvDanhGia.setText(danhGiaText);
                    Glide.with(ChiTietTruyenActivity.this).load(truyen.getAnhBia()).into(ivAnhBia);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadChuongList() {
        // ... code không đổi ...
        database.child("chuong").child(truyenId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chuongList.clear();
                for (DataSnapshot chuongSnapshot : snapshot.getChildren()) {
                    chuongList.add(chuongSnapshot.getValue(Chuong.class));
                }
                chuongAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadBinhLuanList() {
        // ... code không đổi ...
        database.child("binh_luan").child(truyenId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binhLuanList.clear();
                for (DataSnapshot blSnapshot : snapshot.getChildren()) {
                    binhLuanList.add(blSnapshot.getValue(BinhLuan.class));
                }
                binhLuanAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // =================================================================
    // ==================== CÁC PHƯƠNG THỨC MỚI ========================
    // =================================================================

    /**
     * Cài đặt trạng thái ban đầu và sự kiện click cho nút Follow.
     */
    private void setupFollowButton() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Nếu người dùng chưa đăng nhập, vô hiệu hóa nút và yêu cầu đăng nhập
        if (currentUser == null) {
            btnFollow.setText("Đăng nhập để theo dõi");
            btnFollow.setEnabled(false);
            return;
        }

        // Nếu đã đăng nhập, tiến hành kiểm tra trạng thái
        btnFollow.setEnabled(true);
        DatabaseReference libraryRef = database.child("user_library").child(currentUser.getUid()).child(truyenId);

        libraryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // snapshot.exists() sẽ trả về true nếu truyện đã có trong thư viện
                isTruyenInLibrary = snapshot.exists();
                updateFollowButtonState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChiTietTruyenActivity.this, "Lỗi kiểm tra tủ sách", Toast.LENGTH_SHORT).show();
            }
        });

        // Gán sự kiện click
        btnFollow.setOnClickListener(v -> toggleFollowStatus(currentUser.getUid()));
    }

    /**
     * Cập nhật giao diện (văn bản, màu sắc) của nút Follow.
     */
    private void updateFollowButtonState() {
        if (isTruyenInLibrary) {
            btnFollow.setText("Bỏ theo dõi");
            btnFollow.setBackgroundColor(ContextCompat.getColor(this, R.color.grey)); // Màu xám khi đã follow
        } else {
            btnFollow.setText("+ Theo dõi");
            btnFollow.setBackgroundColor(ContextCompat.getColor(this, R.color.green)); // Màu xanh lá khi chưa follow
        }
    }

    /**
     * Xử lý việc thêm hoặc xóa truyện khỏi thư viện (nhấn nút follow/unfollow).
     * @param userId ID của người dùng hiện tại.
     */
    private void toggleFollowStatus(String userId) {
        DatabaseReference libraryRef = database.child("user_library").child(userId).child(truyenId);

        if (isTruyenInLibrary) {
            // Nếu đang follow -> Bỏ follow (xóa khỏi database)
            libraryRef.removeValue().addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Đã bỏ theo dõi", Toast.LENGTH_SHORT).show()
            );
        } else {
            // Nếu chưa follow -> Follow (thêm vào database)
            libraryRef.setValue(true).addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Đã theo dõi", Toast.LENGTH_SHORT).show()
            );
        }
    }
}