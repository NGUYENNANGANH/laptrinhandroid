package com.example.truyenchu.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.truyenchu.R;
import com.example.truyenchu.adapter.FeaturedTruyenAdapter;
import com.example.truyenchu.adapter.RecentUpdatesAdapter;
import com.example.truyenchu.model.Truyen;
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
    // Thêm một Tag để lọc log cho dễ
    private static final String TAG = "HomeFragment";
    private RecyclerView featuredRecyclerView;
    private RecyclerView recentUpdatesRecyclerView;
    private FeaturedTruyenAdapter featuredAdapter;
    private RecentUpdatesAdapter recentAdapter;
    private List<Truyen> featuredTruyenList;
    private List<Truyen> recentTruyenList;
    private DatabaseReference databaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ RecyclerViews
        featuredRecyclerView = view.findViewById(R.id.featured_recycler_view);
        recentUpdatesRecyclerView = view.findViewById(R.id.recent_updates_recycler_view);

        // THAY ĐỔI QUAN TRỌNG:
        // Trỏ đến node "truyen" theo data.json của bạn
        // Lưu ý: Nếu bạn đã cấu hình google-services.json đúng, bạn không cần truyền URL vào getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("truyen");
//        Log.d(TAG, "Database reference: " + databaseReference.toString()); // Log để xem URL

        // Thiết lập RecyclerView cho "Nội dung nổi bật"
        setupFeaturedRecyclerView();

        // Thiết lập RecyclerView cho "Cập nhật gần đây"
        setupRecentUpdatesRecyclerView();

        // Tải dữ liệu từ Firebase
        loadFeaturedStories();
        loadRecentUpdates();
    }

    private void setupFeaturedRecyclerView() {
        featuredRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredTruyenList = new ArrayList<>();
        featuredAdapter = new FeaturedTruyenAdapter(getContext(), featuredTruyenList);
        featuredRecyclerView.setAdapter(featuredAdapter);
        //Set listener cho adapter
        featuredAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void setupRecentUpdatesRecyclerView() {
        recentUpdatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recentTruyenList = new ArrayList<>();
        recentAdapter = new RecentUpdatesAdapter(getContext(), recentTruyenList);
        recentUpdatesRecyclerView.setAdapter(recentAdapter);

        //Set listener cho adapter này, cũng gọi đến hàm chung
        recentAdapter.setOnItemClickListener(this::onItemClick);
    }

    //Hàm chung để xử lý sự kiện click từ cả hai adapter
    private void onItemClick(Truyen truyen) {
        if (truyen == null || truyen.getId() == null) return;

        // 1. Tạo một đối tượng Bundle
        Bundle bundle = new Bundle();

        // 2. Đặt dữ liệu vào bundle với key đã định nghĩa ở ReadingFragment
        bundle.putString(ReadingFragment.KEY_STORY_ID, truyen.getId());

        // 3. Điều hướng bằng ID của action và đính kèm bundle
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_nav_home_to_readingFragment, bundle);
        }
        // Lấy ID của truyện được click
//        String storyId = truyen.getId();
//        // Tạo action điều hướng và truyền ID qua
//        // Tên class và action được sinh ra từ ID trong nav_graph
//        HomeFragmentDirections.ActionNavHomeToReadingFragment action =
//                HomeFragmentDirections.actionNavHomeToReadingFragment(storyId);
//        // Thực hiện điều hướng
//        if (getView() != null) {
//            Navigation.findNavController(getView()).navigate(action);
//        }
    }

    private void loadFeaturedStories() {
        // Lấy 5 truyện có lượt đánh giá cao nhất. Logic này vẫn đúng với data của bạn.
        Query featuredQuery = databaseReference.orderByChild("danhGia").limitToLast(5);
        featuredQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "loadFeaturedStories onDataChange: " + snapshot.getChildrenCount() + " items found.");
                featuredTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        featuredTruyenList.add(truyen);
                    }
                }
                // Đảo ngược danh sách để truyện có đánh giá cao nhất lên đầu
                Collections.reverse(featuredTruyenList);
                featuredAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Log.e(TAG, "loadFeaturedStories onCancelled: ", error.toException());
            }
        });
    }

    private void loadRecentUpdates() {
        // LƯU Ý: Dữ liệu của bạn không có trường "ngày cập nhật" ở trong mỗi object truyện.
        // Do đó, cách đơn giản nhất để giả lập "Cập nhật gần đây" là lấy các truyện
        // được thêm vào database gần nhất.
        // Query này sẽ lấy 10 truyện cuối cùng được thêm vào node "truyen".
        Query recentQuery = databaseReference.limitToLast(10);

        recentQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "loadRecentUpdates onDataChange: " + snapshot.getChildrenCount() + " items found.");
                recentTruyenList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);

                    if (truyen != null) {
                        recentTruyenList.add(truyen);
                    }
                }
                // Đảo ngược danh sách để truyện mới nhất hiển thị lên đầu
                Collections.reverse(recentTruyenList);
                recentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadRecentUpdates onCancelled: ", error.toException());
                // Xử lý lỗi
            }
        });
    }


}