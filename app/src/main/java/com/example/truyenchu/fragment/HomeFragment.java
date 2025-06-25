package com.example.truyenchu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.truyenchu.R;
import com.example.truyenchu.activity.ChiTietTruyenActivity;
import com.example.truyenchu.activity.TimKiemActivity;
import com.example.truyenchu.adapter.FeaturedTruyenAdapter;
import com.example.truyenchu.adapter.RecentUpdatesAdapter;
import com.example.truyenchu.model.Truyen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView featuredRecyclerView;
    private RecyclerView recentUpdatesRecyclerView;
    private FeaturedTruyenAdapter featuredAdapter;
    private RecentUpdatesAdapter recentAdapter;
    private List<Truyen> featuredTruyenList;
    private List<Truyen> recentTruyenList;
    private DatabaseReference databaseReference;
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các views với đúng ID từ file XML của bạn
        featuredRecyclerView = view.findViewById(R.id.featured_recycler_view);
        recentUpdatesRecyclerView = view.findViewById(R.id.recent_updates_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        FloatingActionButton fabChat = view.findViewById(R.id.fab_chat);

        databaseReference = FirebaseDatabase.getInstance().getReference("truyen");

        // Thiết lập RecyclerViews
        setupFeaturedRecyclerView();
        setupRecentUpdatesRecyclerView();

        // Tải dữ liệu
        loadFeaturedStories();
        loadRecentUpdates();

        // Xử lý sự kiện click trên ô tìm kiếm
        searchEditText.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), TimKiemActivity.class)));

        // Xử lý sự kiện click trên nút chatbot
        fabChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng Chatbot sẽ được cập nhật sau", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFeaturedRecyclerView() {
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredTruyenList = new ArrayList<>();
        featuredAdapter = new FeaturedTruyenAdapter(getContext(), featuredTruyenList);
        featuredRecyclerView.setAdapter(featuredAdapter);
        featuredAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void setupRecentUpdatesRecyclerView() {
        recentUpdatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTruyenList = new ArrayList<>();
        recentAdapter = new RecentUpdatesAdapter(getContext(), recentTruyenList);
        recentUpdatesRecyclerView.setAdapter(recentAdapter);
        recentAdapter.setOnItemClickListener(this::onItemClick);
    }

    // Hàm chung để xử lý click, chuyển sang ChiTietTruyenActivity
    private void onItemClick(Truyen truyen) {
        if (truyen == null || truyen.getId() == null) return;

        Intent intent = new Intent(getActivity(), ChiTietTruyenActivity.class);
        intent.putExtra("TRUYEN_ID", truyen.getId());
        startActivity(intent);
    }

    private void loadFeaturedStories() {
        Query featuredQuery = databaseReference.orderByChild("danhGia").limitToLast(5);
        featuredQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featuredTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(dataSnapshot.getKey());
                        featuredTruyenList.add(truyen);
                    }
                }
                Collections.reverse(featuredTruyenList);
                featuredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadFeaturedStories onCancelled: ", error.toException());
            }
        });
    }

    private void loadRecentUpdates() {
        Query recentQuery = databaseReference.limitToLast(10);
        recentQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.setId(dataSnapshot.getKey());
                        recentTruyenList.add(truyen);
                    }
                }
                Collections.reverse(recentTruyenList);
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadRecentUpdates onCancelled: ", error.toException());
            }
        });
    }
}
