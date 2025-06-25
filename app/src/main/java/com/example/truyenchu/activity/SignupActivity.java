package com.example.truyenchu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenchu.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class SignupActivity extends AppCompatActivity {
    private TextInputEditText editDisplayName, editEmail, editPassword;
    private TextView textLoginLink;
    private FirebaseAuth mAuth;

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
                    }
                }
            });

    private FirebaseFirestore db;

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
        textLoginLink = findViewById(R.id.textLoginLink);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        textLoginLink.setOnClickListener(view -> {
        });
    }

    private void validateAndRegisterUser() {

        }
        }
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                });
    }

    private void updateUserInfo(FirebaseUser firebaseUser, String displayName, String email, boolean isFromSocialLogin) {
        String uid = firebaseUser.getUid();

                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignupActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    if (isFromSocialLogin) {
                        goToMainActivity();
                    } else {
                        finishAffinity();
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                        } else {
                            Toast.makeText(this, "Đăng nhập bằng Google thành công!", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        }
                });
    }

    private void goToMainActivity() {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
