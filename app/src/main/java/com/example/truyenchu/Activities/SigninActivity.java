package com.example.truyenchu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.truyenchu.MainActivity;
import com.example.truyenchu.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editPassword;
    private TextView textForgotPassword, textSignupLink;
    private Button btnSignIn, btnGoogle;
    private String email, password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        textSignupLink = findViewById(R.id.textSignupLink);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            if (email != null) {
                editEmail.setText(email);
            }
            if (password != null) {
                editPassword.setText(password);
            }
        }

        textSignupLink.setOnClickListener(view -> {
            Intent intent1 = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent1);
            finishAffinity();
        });

        btnSignIn.setOnClickListener(view -> {
            signInWithEmailAndPassword();
        });
    }

    private void signInWithEmailAndPassword() {
        email = editEmail.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
        }).addOnFailureListener(e -> Log.e(TAG, "Lỗi không thể đăng nhập ", e));
    }
}