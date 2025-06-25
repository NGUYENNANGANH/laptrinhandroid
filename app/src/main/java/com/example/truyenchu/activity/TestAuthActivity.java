package com.example.truyenchu.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenchu.R;
import com.google.firebase.auth.FirebaseAuth;

public class TestAuthActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_auth);

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.test_email);
        editPassword = findViewById(R.id.test_password);
        btnRegister = findViewById(R.id.test_register_button);

        btnRegister.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(TestAuthActivity.this, "Vui lòng nhập cả email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("TestAuth", "Bắt đầu đăng ký với email: " + email);
            Toast.makeText(TestAuthActivity.this, "Đang thử đăng ký...", Toast.LENGTH_SHORT).show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Log.d("TestAuth", "Đăng ký THÀNH CÔNG!");
                        Toast.makeText(TestAuthActivity.this, "TEST THÀNH CÔNG!", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        // Đây là phần quan trọng nhất
                        String errorMessage = e.getMessage();
                        Log.e("TestAuth", "Đăng ký THẤT BẠI: " + errorMessage);
                        Toast.makeText(TestAuthActivity.this, "TEST THẤT BẠI: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
        });
    }
}