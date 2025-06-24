package com.example.truyenchu;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Button btn = findViewById(R.id.buttonHAHA);

//        btn.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
//            startActivity(intent);
//        });
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
//        FloatingActionButton fabChat = findViewById(R.id.fab_chat);

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Kết nối BottomNavigationView với NavController
        // Thao tác này sẽ tự động xử lý việc chuyển fragment khi item được chọn
        NavigationUI.setupWithNavController(navView, navController);

        // Logic ẩn/hiện BottomNavigationView
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Kiểm tra ID của màn hình (fragment) sắp được hiển thị
            if (destination.getId() == R.id.nav_home ||
                    destination.getId() == R.id.nav_categories ||
                    destination.getId() == R.id.nav_community ||
                    destination.getId() == R.id.nav_library ||
                    destination.getId() == R.id.nav_profile)
            {
                // Nếu là 1 trong 5 màn hình chính, cho hiện thanh điều hướng
                navView.setVisibility(View.VISIBLE);
            } else {
                // Nếu là các màn hình khác (như ReadingFragment), ẩn nó đi
                navView.setVisibility(View.GONE);
            }
        });

    }
}