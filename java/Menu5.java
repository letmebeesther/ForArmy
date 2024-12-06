package com.esther.perfect;

import android.os.Bundle;
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

public class Menu5 extends Fragment {

    private RecyclerView recyclerView;
    private AdapterMembers adapter;
    private List<Member> memberList;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu5, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        memberList = new ArrayList<>();
        adapter = new AdapterMembers(this, memberList);
        recyclerView.setAdapter(adapter);

        myRef = FirebaseDatabase.getInstance().getReference().child("member");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String profileImage = snapshot.child("profile").getValue(String.class);
                    String memberText = snapshot.child("memberText").getValue(String.class);
                    String userId = snapshot.child("userID").getValue(String.class);
                    if (profileImage != null && userId != null) {
                        memberList.add(new Member(profileImage, userId, memberText));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기가 취소되었을 때 호출되는 콜백
            }
        });

        return rootView;
    }
}
