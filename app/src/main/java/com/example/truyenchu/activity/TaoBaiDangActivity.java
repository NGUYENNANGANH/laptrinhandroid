package com.example.truyenchu.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.truyenchu.R;
import com.example.truyenchu.model.BaiDang;
import com.example.truyenchu.model.Truyen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaoBaiDangActivity extends AppCompatActivity {

    private ImageView ivAnhBaiDang;
    private EditText etNoiDung;
    private AutoCompleteTextView actvTenTruyen;
    private Button btnDangBai;
    private ProgressBar progressBar;
    private Uri selectedImageUri;

    private Map<String, Truyen> truyenMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_bai_dang);

        ivAnhBaiDang = findViewById(R.id.iv_anh_bai_dang);
        etNoiDung = findViewById(R.id.et_noi_dung_bai_dang);
        actvTenTruyen = findViewById(R.id.actv_ten_truyen);
        btnDangBai = findViewById(R.id.btn_dang_bai);
        progressBar = findViewById(R.id.progressBar_tao_bai_dang);

        loadTruyenSuggestions();

        ivAnhBaiDang.setOnClickListener(v -> openGallery());
        btnDangBai.setOnClickListener(v -> tryDangBai());
    }

    private void loadTruyenSuggestions() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("truyen");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> tenTruyenList = new ArrayList<>();
                truyenMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        tenTruyenList.add(truyen.getTen());
                        truyenMap.put(truyen.getTen(), truyen);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(TaoBaiDangActivity.this,
                        android.R.layout.simple_dropdown_item_1line, tenTruyenList);
                actvTenTruyen.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivAnhBaiDang.setImageURI(selectedImageUri);
                    ivAnhBaiDang.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void tryDangBai() {
        String noiDung = etNoiDung.getText().toString().trim();
        if (noiDung.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung bài đăng", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (selectedImageUri != null) {
            uploadImageAndCreatePost(noiDung);
        } else {
            createPost(noiDung, null);
        }
    }

    private void uploadImageAndCreatePost(String noiDung) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("post_images/" + UUID.randomUUID().toString());

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        createPost(noiDung, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    setLoading(false);
                });
    }

    private void createPost(String noiDung, String imageUrl) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bai_dang");
        String postId = dbRef.push().getKey();
        String tenTruyen = actvTenTruyen.getText().toString().trim();

        BaiDang newPost = new BaiDang();
        newPost.setPostId(postId);
        newPost.setPostContent(noiDung);
        newPost.setTimestamp(System.currentTimeMillis());
        newPost.setUserId("temp_user_id");
        newPost.setUserName("Người dùng ẩn danh");
        newPost.setUserAvatar("https://placehold.co/100x100/2ecc71/ffffff?text=U");

        if (imageUrl != null) {
            newPost.setPostImage(imageUrl);
        }

        if (!tenTruyen.isEmpty() && truyenMap.containsKey(tenTruyen)) {
            Truyen selectedTruyen = truyenMap.get(tenTruyen);
            if (selectedTruyen != null) {
                newPost.setStoryId(selectedTruyen.getId());
                newPost.setStoryName(selectedTruyen.getTen());
                newPost.setStoryAuthor(selectedTruyen.getTacGia());
                newPost.setStoryGenreTags(selectedTruyen.getTheLoaiTags());
                newPost.setStoryCoverImage(selectedTruyen.getAnhBia());
            }
        }

        if (postId != null) {
            dbRef.child(postId).setValue(newPost)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                        setLoading(false);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    });
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnDangBai.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnDangBai.setEnabled(true);
        }
    }
}
