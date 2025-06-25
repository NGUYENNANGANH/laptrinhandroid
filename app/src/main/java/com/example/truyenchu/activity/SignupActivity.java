package com.example.truyenchu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenchu.MainActivity;
import com.example.truyenchu.R;
import com.example.truyenchu.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText editDisplayName, editEmail, editPassword;
    private Button btnSignUp;
    private MaterialButton btnGoogle;
    private TextView textLoginLink;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private GoogleSignInClient mGoogleSignInClient;
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Log.w("GoogleSignIn", "Google sign in failed", e);
                        progressDialog.dismiss();
                        Toast.makeText(this, "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu người dùng hủy, ẩn dialog chờ
                    progressDialog.dismiss();
                }
            });

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ");
        progressDialog.setCanceledOnTouchOutside(false);

        editDisplayName = findViewById(R.id.editDisplayName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoogle = findViewById(R.id.btnGoogle);
        textLoginLink = findViewById(R.id.textLoginLink);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignUp.setOnClickListener(view -> validateAndRegisterUser());
        btnGoogle.setOnClickListener(view -> signInWithGoogle());
        textLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, SigninActivity.class));
        });
    }

    private void validateAndRegisterUser() {
        String displayName = Objects.requireNonNull(editDisplayName.getText()).toString().trim();
        String email = Objects.requireNonNull(editEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(displayName)) {
            editDisplayName.setError("Tên không được để trống");
            editDisplayName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email không hợp lệ");
            editEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            editPassword.requestFocus();
            return;
        }

        registerUser(displayName, email, password);
    }

    private void registerUser(String displayName, String email, String password) {
        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    updateUserInfo(authResult.getUser(), displayName, email, false);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void updateUserInfo(FirebaseUser firebaseUser, String displayName, String email, boolean isFromSocialLogin) {
        progressDialog.setMessage("Đang lưu thông tin...");

        String uid = firebaseUser.getUid();
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setProfileImage("");
        user.setUserType("user");
        user.setTimestamp(System.currentTimeMillis());
        user.setPhoneNumber("");

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    if (isFromSocialLogin) {
                        // Nếu đăng ký qua Google, vào thẳng màn hình chính
                        goToMainActivity();
                    } else {
                        // Nếu đăng ký qua email, quay về màn hình đăng nhập
                        startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Lưu thông tin thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void signInWithGoogle() {
        progressDialog.setMessage("Đang kết nối với Google...");
        progressDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setMessage("Đang xác thực...");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (authResult.getAdditionalUserInfo().isNewUser()) {
                        updateUserInfo(user, user.getDisplayName(), user.getEmail(), true);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Đăng nhập bằng Google thành công!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Xác thực Firebase thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // SỬA ĐỔI 3: Chuyển hướng đến đúng MainActivity
    private void goToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
