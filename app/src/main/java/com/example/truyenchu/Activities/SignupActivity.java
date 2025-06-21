package com.example.truyenchu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.truyenchu.MainActivity;
import com.example.truyenchu.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText editDisplayName, editEmail, editPassword;
    private Button btnSignUp, btnGoogle, btnFacebook;
    private TextView textLoginLink;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private String displayName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editDisplayName = findViewById(R.id.editDisplayName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        textLoginLink = findViewById(R.id.textLoginLink);

        btnSignUp.setOnClickListener(view -> {
            validateAndRegisterUser();
        });

    }

    private void validateAndRegisterUser() {
        displayName = editDisplayName.getText().toString();
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();

        if (displayName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên hiển thị", Toast.LENGTH_SHORT).show();
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateUserInfo();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String uid = firebaseUser.getUid();

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", uid);
        userInfo.put("email", email);
        userInfo.put("displayName", displayName);
        userInfo.put("profileImage", "");
        userInfo.put("userType", "user");
        userInfo.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(uid).set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignupActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Lưu thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
