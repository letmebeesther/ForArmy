package com.esther.perfect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Menu4 extends Fragment {
    private RecyclerView recyclerView;
    private AdapterProduct adapterProduct;
    private List<Product> productList;
    private Handler sliderHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_shop, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapterProduct = new AdapterProduct(getContext(), productList, this); // Menu4 프래그먼트를 전달
        recyclerView.setAdapter(adapterProduct);

        sliderHandler = new Handler(Looper.getMainLooper());

        fetchProductData();

        return rootView;
    }

    private void fetchProductData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("shop");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                adapterProduct.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacksAndMessages(null);
    }

    public void scheduleSlider(final ViewPager2 viewPager) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int itemCount = viewPager.getAdapter().getItemCount();
                int currentItem = viewPager.getCurrentItem();
                int nextItem = currentItem + 1 >= itemCount ? 0 : currentItem + 1;
                viewPager.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // 3초 간격으로 슬라이드
            }
        };
        sliderHandler.postDelayed(runnable, 3000); // 3초 후에 첫 슬라이드 시작
    }
}
