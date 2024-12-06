package com.esther.perfect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AdapterViewPager2 extends RecyclerView.Adapter<AdapterViewPager2.ViewHolder> {

    private Context context;
    private List<String> imageUrls;

    public AdapterViewPager2(Context context, List<String> imageUrls) {
        this.context = context;
        // 첫 번째 이미지를 제외한 나머지 이미지를 리스트에 담기
        if (imageUrls.size() > 1) {
            this.imageUrls = imageUrls.subList(1, imageUrls.size());
        } else {
            this.imageUrls = imageUrls;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_image_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Glide를 사용하여 이미지를 로드
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
