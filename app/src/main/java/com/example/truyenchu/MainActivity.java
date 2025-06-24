package com.example.truyenchu;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.truyenchu.R;
import com.example.truyenchu.fragment.CategoryFragment;
import com.example.truyenchu.fragment.CongDongFragment;
//import com.example.truyenchu.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // THAY ĐỔI Ở ĐÂY:
        // Đặt CategoryFragment làm màn hình mặc định để tránh lỗi
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_category); // Đặt tab Category được highlight
            loadFragment(new CategoryFragment()); // Tải CategoryFragment lên màn hình
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_category) {
                    // Khi bạn của bạn làm xong, dòng này sẽ hoạt động
//                    selectedFragment = new HomeFragment();
                    selectedFragment = new CategoryFragment();
//                } else if (itemId == R.id.nav_category) {
//                    selectedFragment = new CategoryFragment();
                } else if (itemId == R.id.nav_community) {
                    selectedFragment = new CongDongFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            };

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
