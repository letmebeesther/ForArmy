package com.esther.perfect;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterMemberGrid extends BaseAdapter {
    private Context context;
    private List<String> imageUrls;

    public AdapterMemberGrid(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.grid_item_image);
        String imageUrl = imageUrls.get(position);

        Log.d("AdapterMemberGrid", "Loading image: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        return convertView;
    }
}
