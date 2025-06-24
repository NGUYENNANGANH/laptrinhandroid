package com.example.truyenchu; // Gói package của bạn là đúng

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.truyenchu.R;
import com.example.truyenchu.fragment.CategoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Xóa các import không cần thiết như EdgeToEdge, Insets...
// và thêm các import mới cho Fragment và BottomNavigationView.

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout activity_main.xml mà chúng ta đã tạo
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Đặt mục "Phân loại" làm mục được chọn mặc định khi mở app
        bottomNav.setSelectedItemId(R.id.nav_category);

        // Gán sự kiện listener để xử lý khi người dùng chọn một mục
        bottomNav.setOnItemSelectedListener(navListener);

        // Hiển thị Fragment mặc định khi ứng dụng khởi động lần đầu
        if (savedInstanceState == null) {
            loadFragment(new CategoryFragment());
        }
    }

    // Listener để xử lý sự kiện click trên BottomNavigationView
    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                // Kiểm tra xem người dùng đã chọn mục nào
                if (itemId == R.id.nav_category) {
                    selectedFragment = new CategoryFragment();
                }
                // Trong tương lai, bạn có thể thêm các Fragment khác ở đây
                // Ví dụ:
                // else if (itemId == R.id.nav_home) {
                //    selectedFragment = new HomeFragment();
                // }
                // else if (itemId == R.id.nav_library) {
                //    selectedFragment = new LibraryFragment();
                // }

                // Nếu có fragment được chọn, tải nó lên màn hình
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            };

    // Hàm để thay thế Fragment đang hiển thị trong FrameLayout
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
