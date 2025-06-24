package com.example.truyenchu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.truyenchu.Activities.SignupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

//        fabChat.setOnClickListener(v -> {
//            // TODO: Hiển thị popup chat ở đây
//            Toast.makeText(MainActivity.this, "Mở popup chat!", Toast.LENGTH_SHORT).show();
//        });
    }
}