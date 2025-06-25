package com.example.truyenchu; // Thay bằng package của bạn

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.truyenchu.R;
import com.example.truyenchu.fragment.HomeFragment;
import com.example.truyenchu.fragment.ProfileFragment;
import com.example.truyenchu.fragment.CategoryFragment;
import com.example.truyenchu.fragment.CongDongFragment;
// import com.example.truyenchu.fragment.LibraryFragment; // Bạn cần tạo Fragment này
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // SỬA ĐỔI: Sử dụng đúng các ID từ file menu của bạn
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (itemId == R.id.nav_community) {
                selectedFragment = new CongDongFragment();
            }
            // else if (itemId == R.id.nav_library) {
            //     selectedFragment = new LibraryFragment(); // Fragment cho Tủ sách
            // }
            else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Kiểm tra xem có yêu cầu chuyển đến Profile tab không
        boolean navigateToProfile = getIntent().getBooleanExtra("NAVIGATE_TO_PROFILE", false);
        
        if (navigateToProfile) {
            // Chuyển đến tab Profile sau khi đăng nhập thành công
            bottomNav.setSelectedItemId(R.id.nav_profile);
        } else if (savedInstanceState == null) {
            // Đặt HomeFragment làm màn hình mặc định khi khởi động bình thường
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
