package com.example.truyenchu.activity;

import android.content.Intent; // THÊM MỚI
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenchu.R;
import com.example.truyenchu.adapter.BinhLuanAdapter;
import com.example.truyenchu.adapter.ChuongAdapter;
import com.example.truyenchu.fragment.ReadingFragment; // THÊM MỚI
import com.example.truyenchu.model.BinhLuan;
import com.example.truyenchu.model.Chuong;
import com.example.truyenchu.model.Truyen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private Button btnFollow;
    private Button btnRead; // THÊM MỚI
    private FirebaseAuth mAuth;
    private boolean isTruyenInLibrary = false;


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
        mAuth = FirebaseAuth.getInstance();

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
        btnFollow = findViewById(R.id.btn_follow);
        btnRead = findViewById(R.id.btn_read); // THÊM MỚI
    }

    private void setupRecyclerViews() {
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
        setupFollowButton();
        setupReadButton(); // THÊM MỚI
    }


    // =================================================================
    // =========== CẬP NHẬT LOGIC NÚT ĐỌC TRUYỆN =======================
    // =================================================================

    private void setupReadButton() {
        btnRead.setOnClickListener(v -> {
            // 1. Tạo một instance của ReadingFragment
            ReadingFragment readingFragment = new ReadingFragment();

            // 2. Tạo Bundle để truyền truyenId sang
            Bundle bundle = new Bundle();
            bundle.putString(ReadingFragment.KEY_STORY_ID, truyenId);
            readingFragment.setArguments(bundle);

            // 3. Thực hiện việc thay thế toàn bộ giao diện của Activity bằng Fragment
            getSupportFragmentManager().beginTransaction()
                    // android.R.id.content là ID của layout gốc chứa toàn bộ giao diện của Activity
                    .replace(android.R.id.content, readingFragment)
                    // RẤT QUAN TRỌNG: Thêm giao dịch này vào back stack
                    // để khi người dùng nhấn nút Back, nó sẽ quay lại màn hình chi tiết
                    .addToBackStack(null)
                    .commit();
        });
    }


    // ... Các phương thức load data và xử lý nút follow không thay đổi ...

    private void loadTruyenInfo() {
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
    private void setupFollowButton() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            btnFollow.setText("Đăng nhập để theo dõi");
            btnFollow.setEnabled(false);
            return;
        }

        btnFollow.setEnabled(true);
        DatabaseReference libraryRef = database.child("user_library").child(currentUser.getUid()).child(truyenId);

        libraryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isTruyenInLibrary = snapshot.exists();
                updateFollowButtonState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChiTietTruyenActivity.this, "Lỗi kiểm tra tủ sách", Toast.LENGTH_SHORT).show();
            }
        });

        btnFollow.setOnClickListener(v -> toggleFollowStatus(currentUser.getUid()));
    }

    private void updateFollowButtonState() {
        if (isTruyenInLibrary) {
            btnFollow.setText("Bỏ theo dõi");
            btnFollow.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        } else {
            btnFollow.setText("+ Theo dõi");
            btnFollow.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }
    private void toggleFollowStatus(String userId) {
        DatabaseReference libraryRef = database.child("user_library").child(userId).child(truyenId);

        if (isTruyenInLibrary) {
            libraryRef.removeValue().addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Đã bỏ theo dõi", Toast.LENGTH_SHORT).show()
            );
        } else {
            libraryRef.setValue(true).addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Đã theo dõi", Toast.LENGTH_SHORT).show()
            );
        }
    }
}