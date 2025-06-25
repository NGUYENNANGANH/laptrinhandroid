package com.example.truyenchu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // THÊM MỚI
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide; // THÊM MỚI
import com.example.truyenchu.R;
import com.example.truyenchu.activity.ChiTietTruyenActivity;
import com.example.truyenchu.activity.SigninActivity; // THÊM MỚI
import com.example.truyenchu.activity.TimKiemActivity;
import com.example.truyenchu.adapter.FeaturedTruyenAdapter;
import com.example.truyenchu.adapter.RecentUpdatesAdapter;
import com.example.truyenchu.adapter.StorySliderAdapter;
import com.example.truyenchu.model.Truyen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth; // THÊM MỚI
import com.google.firebase.auth.FirebaseUser; // THÊM MỚI
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView; // THÊM MỚI

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // Views
    private RecyclerView featuredRecyclerView;
    private RecyclerView recentUpdatesRecyclerView;
    private EditText searchEditText;
    private ViewPager2 viewPager;
    private TabLayout tabLayoutIndicator;
    private FloatingActionButton fabChat;
    private CircleImageView profileImage; // THÊM MỚI
    private Button btnLogin; // THÊM MỚI

    // Adapters
    private FeaturedTruyenAdapter featuredAdapter;
    private RecentUpdatesAdapter recentAdapter;
    private StorySliderAdapter sliderAdapter;

    // Data Lists
    private List<Truyen> featuredTruyenList;
    private List<Truyen> recentTruyenList;
    private List<Truyen> sliderTruyenList;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth; // THÊM MỚI

    // Auto-slide handler
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các views
        featuredRecyclerView = view.findViewById(R.id.featured_recycler_view);
        recentUpdatesRecyclerView = view.findViewById(R.id.recent_updates_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        viewPager = view.findViewById(R.id.view_pager_hero_section);
        tabLayoutIndicator = view.findViewById(R.id.tab_layout_indicator);
        fabChat = view.findViewById(R.id.fab_chat);
        profileImage = view.findViewById(R.id.profile_image); // THÊM MỚI
        btnLogin = view.findViewById(R.id.btn_login_home); // THÊM MỚI

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("truyen");
        mAuth = FirebaseAuth.getInstance(); // THÊM MỚI

        // Thiết lập tất cả các view
        setupSlider();
        setupFeaturedRecyclerView();
        setupRecentUpdatesRecyclerView();

        // Tải dữ liệu cho các danh sách truyện
        loadFeaturedStories();
        loadRecentUpdates();

        // Xử lý sự kiện click
        searchEditText.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TimKiemActivity.class)));

        fabChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng Chatbot sẽ được cập nhật sau", Toast.LENGTH_SHORT).show();
        });
    }

    // CẬP NHẬT: Thêm onResume để kiểm tra lại trạng thái đăng nhập khi quay lại Fragment
    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus(); // Kiểm tra trạng thái người dùng
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    /**
     * THÊM MỚI: Phương thức kiểm tra trạng thái đăng nhập và cập nhật UI
     */
    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User đã đăng nhập
            btnLogin.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);

            // Tải ảnh đại diện của user
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.default_avatar) // Ảnh chờ
                        .error(R.drawable.default_avatar) // Ảnh lỗi
                        .into(profileImage);
            }

            // Xử lý sự kiện click vào ảnh đại diện
            profileImage.setOnClickListener(v -> {
                // Chuyển sang ProfileFragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()) // Thay R.id.fragment_container bằng ID của container chứa fragment trong Activity của bạn
                            .addToBackStack(null)
                            .commit();
                }
            });

        } else {
            // User chưa đăng nhập
            btnLogin.setVisibility(View.VISIBLE);
            profileImage.setVisibility(View.GONE);

            // Xử lý sự kiện click vào nút đăng nhập
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), SigninActivity.class));
            });
        }
    }


    private void setupSlider() {
        // ... code không đổi
        sliderTruyenList = new ArrayList<>();
        sliderAdapter = new StorySliderAdapter(getContext(), sliderTruyenList, this::onItemClick);
        viewPager.setAdapter(sliderAdapter);
        new TabLayoutMediator(tabLayoutIndicator, viewPager, (tab, position) -> {}).attach();
        sliderRunnable = () -> {
            if (sliderAdapter.getItemCount() > 0) {
                int currentItem = viewPager.getCurrentItem();
                viewPager.setCurrentItem((currentItem + 1) % sliderAdapter.getItemCount(), true);
            }
        };
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void setupFeaturedRecyclerView() {
        // ... code không đổi
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredTruyenList = new ArrayList<>();
        featuredAdapter = new FeaturedTruyenAdapter(getContext(), featuredTruyenList);
        featuredRecyclerView.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void setupRecentUpdatesRecyclerView() {
        // ... code không đổi
        recentUpdatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTruyenList = new ArrayList<>();
        recentAdapter = new RecentUpdatesAdapter(getContext(), recentTruyenList);
        recentUpdatesRecyclerView.setAdapter(recentAdapter);
        recentAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(Truyen truyen) {
        // ... code không đổi
        if (truyen == null || truyen.getId() == null) return;
        Intent intent = new Intent(getActivity(), ChiTietTruyenActivity.class);
        intent.putExtra("TRUYEN_ID", truyen.getId());
        startActivity(intent);
    }

    private void loadFeaturedStories() {
        // ... code không đổi
        Query featuredQuery = databaseReference.orderByChild("danhGia").limitToLast(5);
        featuredQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featuredTruyenList.clear();
                sliderTruyenList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(dataSnapshot.getKey());
                        featuredTruyenList.add(truyen);
                        if (truyen.getBannerImage() != null && !truyen.getBannerImage().isEmpty()) {
                            sliderTruyenList.add(truyen);
                        }
                    }
                }
                Collections.reverse(featuredTruyenList);
                Collections.reverse(sliderTruyenList);
                featuredAdapter.notifyDataSetChanged();
                sliderAdapter.notifyDataSetChanged();
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadFeaturedStories onCancelled: ", error.toException());
            }
        });
    }

    private void loadRecentUpdates() {
        // ... code không đổi
        Query recentQuery = databaseReference.limitToLast(10);
        recentQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(dataSnapshot.getKey());
                        recentTruyenList.add(truyen);
                    }
                }
                Collections.reverse(recentTruyenList);
                recentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadRecentUpdates onCancelled: ", error.toException());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}