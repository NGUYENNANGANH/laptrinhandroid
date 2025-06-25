package com.example.truyenchu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenchu.R;
import com.example.truyenchu.activity.ChiTietTruyenActivity;
import com.example.truyenchu.activity.SigninActivity;
import com.example.truyenchu.adapter.LibraryAdapter;
import com.example.truyenchu.model.Truyen;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private LinearLayout libraryContentLayout;
    private LinearLayout loggedOutLayout;
    private MaterialButton btnLogin;
    private RecyclerView recyclerView;
    private LibraryAdapter adapter;
    private List<Truyen> truyenList;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        libraryContentLayout = view.findViewById(R.id.library_content_layout);
        loggedOutLayout = view.findViewById(R.id.logged_out_layout);
        btnLogin = view.findViewById(R.id.btn_login_from_library);
        recyclerView = view.findViewById(R.id.library_recycler_view);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        truyenList = new ArrayList<>();
        adapter = new LibraryAdapter(getContext(), truyenList);
        recyclerView.setAdapter(adapter);

        // << THÊM DÒNG NÀY ĐỂ ĐĂNG KÝ LISTENER
        adapter.setOnItemClickListener(this::onItemClick);

    }

    private void onItemClick(Truyen truyen) {
        // Kiểm tra để tránh lỗi
        if (truyen == null || truyen.getId() == null) {
            Toast.makeText(getContext(), "Không thể mở truyện này", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tạo Intent để chuyển sang ChiTietTruyenActivity
        Intent intent = new Intent(getActivity(), ChiTietTruyenActivity.class);
        // Gửi ID của truyện được click
        intent.putExtra("TRUYEN_ID", truyen.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in
            loggedOutLayout.setVisibility(View.GONE);
            libraryContentLayout.setVisibility(View.VISIBLE);
            loadLibraryData(currentUser.getUid());
        } else {
            // User is not logged in
            loggedOutLayout.setVisibility(View.VISIBLE);
            libraryContentLayout.setVisibility(View.GONE);

            btnLogin.setOnClickListener(v -> {
                Log.d("LibraryFragment123123", "Button login clicked");
                Intent intent123 = new Intent(getContext(), SigninActivity.class);
                startActivity(intent123);
            });
        }
    }

    private void loadLibraryData(String userId) {
        // Assume you have a node like /user_library/{userId}/{truyenId}
        DatabaseReference libraryRef = dbRef.child("user_library").child(userId);
        libraryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                truyenList.clear();
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "Tủ sách của bạn trống", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot truyenIdSnapshot : snapshot.getChildren()) {
                    String truyenId = truyenIdSnapshot.getKey();
                    if (truyenId != null) {
                        // Fetch details for each story from the "truyen" node
                        dbRef.child("truyen").child(truyenId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot truyenSnapshot) {
                                Truyen truyen = truyenSnapshot.getValue(Truyen.class);
                                if (truyen != null) {
                                    truyen.setId(truyenSnapshot.getKey());
                                    truyenList.add(truyen);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load story details.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load library.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
