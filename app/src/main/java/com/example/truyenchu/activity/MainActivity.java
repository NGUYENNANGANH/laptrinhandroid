package com.example.truyenchu.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.truyenchu.R;
import com.example.truyenchu.fragment.CategoryFragment; // Đổi import
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Hiển thị Fragment mặc định khi ứng dụng khởi động
        if (savedInstanceState == null) {
            loadFragment(new CategoryFragment()); // Đổi tên Fragment được load
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_category) {
                    selectedFragment = new CategoryFragment(); // Đổi tên Fragment được chọn
                }
                // Bạn có thể thêm các Fragment khác ở đây
                // else if (itemId == R.id.nav_home) {
                //    selectedFragment = new HomeFragment();
                // }
                // else if (itemId == R.id.nav_library) {
                //    selectedFragment = new LibraryFragment();
                // }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            };

    // Hàm để thay thế Fragment trong FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
