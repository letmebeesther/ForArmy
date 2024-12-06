package com.esther.perfect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Menu2 extends Fragment {

    private static final String TAG = "Menu2";

    private RecyclerView recyclerView;
    private AdapterPoster adapter;
    private List<Poster> posterList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.menu2, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewMenu2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        posterList = new ArrayList<>();
        adapter = new AdapterPoster(posterList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ticket");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posterList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Poster poster = snapshot.getValue(Poster.class);
                    if (poster != null) {
                        Log.d(TAG, "Poster: " + poster.getTitle());
                        posterList.add(poster);
                    } else {
                        Log.d(TAG, "Poster is null");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read data", databaseError.toException());
            }
        });

        return rootView;
    }
}
