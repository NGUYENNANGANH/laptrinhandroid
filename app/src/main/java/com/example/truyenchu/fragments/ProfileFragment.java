package com.example.truyenchu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.truyenchu.Activities.EditProfileActivity;
import com.example.truyenchu.Activities.SigninActivity;
import com.example.truyenchu.R;
import com.example.truyenchu.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    // Khai báo các view
    private CircleImageView avatarIv;
    private TextView nameTv, emailTv, editTv, logoutTv;
    private LinearLayout profileHeaderLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ views
        avatarIv = view.findViewById(R.id.profile_avatar_iv);
        nameTv = view.findViewById(R.id.profile_name_tv);
        emailTv = view.findViewById(R.id.profile_email_tv);
        editTv = view.findViewById(R.id.profile_edit_tv);
        logoutTv = view.findViewById(R.id.profile_logout_tv);
        profileHeaderLayout = view.findViewById(R.id.profile_header_layout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra trạng thái người dùng mỗi khi Fragment được hiển thị
        checkUserStatus();
    }

    private void checkUserStatus() {
        // Reset các listener để tránh hành vi cũ còn sót lại
        logoutTv.setOnClickListener(null);
        profileHeaderLayout.setOnClickListener(null);
        editTv.setOnClickListener(null);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            // --- TRƯỜNG HỢP: ĐÃ ĐĂNG NHẬP ---
            // Lấy thông tin từ Firestore để đảm bảo dữ liệu luôn mới nhất
            DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (isAdded() && documentSnapshot.exists()) { // isAdded() để tránh crash nếu fragment đã bị detach
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        nameTv.setText(user.getDisplayName());
                        emailTv.setText(user.getEmail());
                        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                            Glide.with(ProfileFragment.this).load(user.getProfileImage()).into(avatarIv);
                        } else {
                            avatarIv.setImageResource(R.drawable.ic_avatar_placeholder);
                        }
                    }
                }
            });

            profileHeaderLayout.setClickable(false); // Vô hiệu hóa click trên header
            editTv.setVisibility(View.VISIBLE);
            logoutTv.setVisibility(View.VISIBLE);

            // Gán sự kiện cho nút Đăng xuất
            logoutTv.setOnClickListener(v -> {
                mAuth.signOut();
                checkUserStatus(); // Cập nhật lại UI sau khi đăng xuất
                Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            });

            // Gán sự kiện cho nút Chỉnh sửa hồ sơ
            editTv.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            });

        } else {
            // --- TRƯỜNG HỢP: CHƯA ĐĂNG NHẬP (KHÁCH) ---
            nameTv.setText("Đăng nhập/Đăng ký");
            emailTv.setText("Tham gia cộng đồng để có trải nghiệm tốt nhất");
            avatarIv.setImageResource(R.drawable.ic_avatar_placeholder);
            profileHeaderLayout.setClickable(true); // Cho phép click trên header

            editTv.setVisibility(View.GONE);
            logoutTv.setVisibility(View.GONE);

            profileHeaderLayout.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), SigninActivity.class));
                }
            });
        }
    }
}
