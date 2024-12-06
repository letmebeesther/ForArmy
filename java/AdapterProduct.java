package com.esther.perfect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private Menu4 fragment;

    public AdapterProduct(Context context, List<Product> productList, Menu4 fragment) {
        this.context = context;
        this.productList = productList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.titleView.setText(product.getTitle());
        holder.textView.setText(product.getText());

        // ViewPager2 어댑터 설정
        AdapterViewPager2 adapterViewPager2 = new AdapterViewPager2(context, product.getMain());
        holder.viewPager.setAdapter(adapterViewPager2);

        // TabLayoutMediator를 사용하여 TabLayout을 ViewPager2에 연결
        new TabLayoutMediator(holder.tabLayout, holder.viewPager, (tab, pos) -> {
            // Custom tab indicator if needed
        }).attach();

        // 슬라이더 일정 간격으로 자동 이동 설정
        fragment.scheduleSlider(holder.viewPager);

        holder.goButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager;
        TabLayout tabLayout;
        TextView titleView;
        TextView textView;
        ImageButton goButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.shop_viewPager);
            tabLayout = itemView.findViewById(R.id.shop_tab_layout);
            titleView = itemView.findViewById(R.id.shop_title_view);
            textView = itemView.findViewById(R.id.shop_text_view);
            goButton = itemView.findViewById(R.id.goButton);
        }
    }
}
