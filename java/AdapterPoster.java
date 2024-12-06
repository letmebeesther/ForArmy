package com.esther.perfect;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterPoster extends RecyclerView.Adapter<AdapterPoster.PosterViewHolder> {

    private List<Poster> posterList;

    public AdapterPoster(List<Poster> posterList) {
        this.posterList = posterList;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Poster poster = posterList.get(position);
        holder.posterTitle.setText(poster.getTitle());
        holder.posterDate.setText(poster.getDate());
        holder.posterLocation.setText(poster.getLocation());
        Glide.with(holder.posterImage.getContext())
                .load(poster.getImg())
                .into(holder.posterImage);

        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(poster.getLink()));
            v.getContext().startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }

    static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView posterTitle;
        TextView posterDate;
        TextView posterLocation;

        PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            posterTitle = itemView.findViewById(R.id.poster_title);
            posterDate = itemView.findViewById(R.id.poster_date);
            posterLocation = itemView.findViewById(R.id.poster_location);
        }
    }
}
