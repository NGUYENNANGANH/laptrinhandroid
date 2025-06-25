// HomeFragment.java (Full code - Đã sửa lỗi và tăng cường sự ổn định)
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast; // Thêm import cho Toast

import com.bumptech.glide.Glide;
import com.example.truyenchu.MainActivity;
import com.example.truyenchu.R;
import com.example.truyenchu.activity.ChiTietTruyenActivity;
import com.example.truyenchu.activity.SigninActivity;
import com.example.truyenchu.activity.TimKiemActivity;
import com.example.truyenchu.adapter.FeaturedTruyenAdapter;
import com.example.truyenchu.adapter.RecentUpdatesAdapter;
import com.example.truyenchu.adapter.StorySliderAdapter;
import com.example.truyenchu.model.Truyen;
import com.example.truyenchu.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // Views
    private RecyclerView featuredRecyclerView;
    private RecyclerView recentUpdatesRecyclerView;
    private EditText searchEditText;
    private ViewPager2 viewPager;
    private TabLayout tabLayoutIndicator;
    private FloatingActionButton fabChat;
    private CircleImageView profileImage;
    private ImageView btnNotifications;


    // Adapters
    private FeaturedTruyenAdapter featuredAdapter;
    private RecentUpdatesAdapter recentAdapter;
    private StorySliderAdapter sliderAdapter;

    // Data Lists
    private List<Truyen> featuredTruyenList;
    private List<Truyen> recentTruyenList;
    private List<Truyen> sliderTruyenList;

    private DatabaseReference databaseReference;

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
        profileImage = view.findViewById(R.id.profile_image);
        btnNotifications = view.findViewById(R.id.btn_notifications);

        // Kiểm tra xem các view quan trọng đã được khởi tạo chưa
        if (profileImage == null) {
            Log.e(TAG, "profileImage not found in layout!");
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("truyen");

        // Thiết lập tất cả các view
        setupSlider();
        setupFeaturedRecyclerView();
        setupRecentUpdatesRecyclerView();

        // Tải dữ liệu cho các danh sách truyện
        loadFeaturedStories();
        loadRecentUpdates();

        // Xử lý sự kiện click
        setupClickListeners();
    }

    private void setupClickListeners() {
        searchEditText.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TimKiemActivity.class)));

        fabChat.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                ChatbotDialogFragment chatbotDialog = new ChatbotDialogFragment();
                chatbotDialog.show(getParentFragmentManager(), "ChatbotDialogFragment_Tag");
            }
        });

        // Kiểm tra profileImage có null không trước khi set click listener
        if (profileImage != null) {
            // SỬA LỖI: Bổ sung try-catch và kiểm tra null safety để tránh crash
            profileImage.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Avatar clicked!");
                    
                    if (getActivity() == null || !isAdded()) {
                        Log.w(TAG, "Fragment not attached or activity is null");
                        return;
                    }

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "Current user: " + (currentUser != null ? currentUser.getUid() : "null"));
                    
                    if (currentUser != null) {
                        // Nếu đã đăng nhập, chuyển đến tab Profile
                        Log.d(TAG, "User is logged in, navigating to profile tab");
                        navigateToProfileTab();
                    } else {
                        // Nếu chưa đăng nhập, chuyển đến trang đăng nhập
                        Log.d(TAG, "User not logged in, navigating to sign in");
                        Toast.makeText(getContext(), "Đang chuyển đến trang đăng nhập...", Toast.LENGTH_SHORT).show();
                        navigateToSignIn();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling avatar click: ", e);
                    Toast.makeText(getContext(), "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "profileImage is null, cannot set click listener");
        }

        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng thông báo sẽ được cập nhật sau.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Phương thức riêng để xử lý chuyển đến tab Profile
     */
    private void navigateToProfileTab() {
        try {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BottomNavigationView bottomNav = mainActivity.findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_profile);
                } else {
                    Log.w(TAG, "BottomNavigationView not found");
                }
            } else {
                Log.w(TAG, "Activity is not MainActivity");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to profile tab: ", e);
        }
    }

    /**
     * Phương thức riêng để xử lý chuyển đến trang đăng nhập
     */
    private void navigateToSignIn() {
        try {
            Log.d(TAG, "navigateToSignIn called");
            
            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate to SignIn");
                return;
            }
            
            if (!isAdded()) {
                Log.e(TAG, "Fragment not added, cannot navigate to SignIn");
                return;
            }
            
            Log.d(TAG, "Creating intent for SigninActivity");
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            
            Log.d(TAG, "Starting SigninActivity");
            startActivity(intent);
            
            Log.d(TAG, "SigninActivity started successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to sign in: ", e);
            Toast.makeText(getContext(), "Không thể mở trang đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSlider() {
        sliderTruyenList = new ArrayList<>();
        sliderAdapter = new StorySliderAdapter(getContext(), sliderTruyenList, this::onItemClick);
        viewPager.setAdapter(sliderAdapter);
        new TabLayoutMediator(tabLayoutIndicator, viewPager, (tab, position) -> {}).attach();
        sliderRunnable = () -> {
            if (sliderAdapter != null && sliderAdapter.getItemCount() > 0) {
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
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredTruyenList = new ArrayList<>();
        featuredAdapter = new FeaturedTruyenAdapter(getContext(), featuredTruyenList);
        featuredRecyclerView.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void setupRecentUpdatesRecyclerView() {
        recentUpdatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTruyenList = new ArrayList<>();
        recentAdapter = new RecentUpdatesAdapter(getContext(), recentTruyenList);
        recentUpdatesRecyclerView.setAdapter(recentAdapter);
        recentAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(Truyen truyen) {
        if (truyen == null || truyen.getId() == null || getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ChiTietTruyenActivity.class);
        intent.putExtra("TRUYEN_ID", truyen.getId());
        startActivity(intent);
    }

    private void loadFeaturedStories() {
        Query featuredQuery = databaseReference.orderByChild("danhGia").limitToLast(5);
        featuredQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getContext() == null) return;
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

                if (featuredAdapter != null) featuredAdapter.notifyDataSetChanged();
                if (sliderAdapter != null) sliderAdapter.notifyDataSetChanged();

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
        Query recentQuery = databaseReference.limitToLast(10);
        recentQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                recentTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(dataSnapshot.getKey());
                        recentTruyenList.add(truyen);
                    }
                }
                Collections.reverse(recentTruyenList);
                if (recentAdapter != null) recentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadRecentUpdates onCancelled: ", error.toException());
            }
        });
    }

    private void loadUserInfoToAvatar() {
        try {
            if (!isAdded() || getContext() == null) {
                Log.w(TAG, "Fragment not attached, skipping avatar load");
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Người dùng đã đăng nhập - tải ảnh đại diện từ Firestore
                FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (!isAdded() || getContext() == null) {
                                return;
                            }

                            try {
                                if (documentSnapshot.exists()) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null && user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                                        Glide.with(getContext()).load(user.getProfileImage()).into(profileImage);
                                    } else {
                                        // Người dùng đã đăng nhập nhưng chưa có ảnh đại diện
                                        profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
                                    }
                                } else {
                                    profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error setting user avatar: ", e);
                                if (isAdded()) {
                                    profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error loading user from Firestore: ", e);
                            if (isAdded()) {
                                profileImage.setImageResource(R.drawable.ic_avatar_placeholder);
                            }
                        });
            } else {
                // Người dùng chưa đăng nhập - hiển thị icon mặc định để khuyến khích đăng nhập
                if (isAdded()) {
                    profileImage.setImageResource(R.drawable.default_avatar); // Sử dụng default_avatar có sẵn
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUserInfoToAvatar: ", e);
            if (isAdded()) {
                profileImage.setImageResource(R.drawable.default_avatar);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Cập nhật avatar mỗi khi fragment được hiển thị
        loadUserInfoToAvatar();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfoToAvatar();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    /**
     * Phương thức public để refresh avatar từ bên ngoài (có thể gọi từ MainActivity)
     */
    public void refreshUserAvatar() {
        loadUserInfoToAvatar();
    }
}
