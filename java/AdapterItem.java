package com.esther.perfect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private Menu1 menu1;

    public AdapterItem(Context context, List<Item> itemList, Menu1 menu1) {
        this.context = context;
        this.itemList = itemList;
        this.menu1 = menu1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.userIdTextView.setText(item.getUserId());
        holder.contentTextView.setText(item.getContent());

        // Glide를 사용하여 프로필 이미지를 로드
        Glide.with(context)
                .load(item.getProfileImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImageView);

        // ViewPager2를 설정
        AdapterViewPager adapterViewPager = new AdapterViewPager(context, item.getMainImageUrls());
        holder.viewPager.setAdapter(adapterViewPager);

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(holder.tabLayout, holder.viewPager,
                (tab, position1) -> {
                    // Customizing tabs if needed
                }).attach();

        // 저장 여부에 따라 버튼 이미지 설정
        updateSaveButton(holder.saveButton, item.isSaved());

        holder.saveButton.setOnClickListener(v -> {
            holder.saveButton.setEnabled(false); // 중복 클릭 방지
            menu1.save(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void updateSaveButton(ImageButton saveButton, boolean isSaved) {
        Drawable currentDrawable = saveButton.getDrawable();
        Drawable savedDrawable = ContextCompat.getDrawable(context, R.drawable.check);
        Drawable unsavedDrawable = ContextCompat.getDrawable(context, R.drawable.white_heart);

        // 현재 드로어블 상태와 비교하여 업데이트
        if ((isSaved && currentDrawable.getConstantState() != savedDrawable.getConstantState()) ||
                (!isSaved && currentDrawable.getConstantState() != unsavedDrawable.getConstantState())) {
            int imageResId = isSaved ? R.drawable.check : R.drawable.white_heart;
            saveButton.setImageResource(imageResId);
        }

        saveButton.setEnabled(true);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView contentTextView;
        ImageView profileImageView;
        ViewPager2 viewPager;
        TabLayout tabLayout;
        ImageButton saveButton;

        public ViewHolder(View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.user_id_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            viewPager = itemView.findViewById(R.id.viewPager);
            tabLayout = itemView.findViewById(R.id.tab_layout);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}
