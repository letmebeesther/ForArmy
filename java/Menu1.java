package com.esther.perfect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu1 extends Fragment {

    private RecyclerView recyclerView;
    private AdapterItem adapter;
    private List<Item> itemList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.menu1, container, false);

        // RecyclerView 초기화
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterItem(getContext(), itemList, this);
        recyclerView.setAdapter(adapter);

        // Firebase Realtime Database의 루트 참조
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 데이터베이스 쿼리 설정 (timestamp 기준 정렬)
        Query query = databaseReference.child("member").orderByChild("timestamp");

        // 데이터베이스 리스너 설정
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                handleDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기가 취소되었을 때 호출되는 콜백
                Toast.makeText(getContext(), "Failed to read data.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // 데이터 처리 메서드
    private void handleDataChange(DataSnapshot dataSnapshot) {
        itemList.clear(); // 기존 아이템을 모두 제거

        // 현재 사용자 UID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Menu1", "사용자가 인증되지 않았습니다.");
            return;
        }

        String currentUserUid = currentUser.getUid();

        // "member" 키 아래의 각 사용자 ID를 반복합니다.
        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
            String userId = userSnapshot.getKey(); // 각 키를 사용자 ID로 설정합니다.

            // 각 사용자 ID 아래의 번호 키값을 반복합니다.
            for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                if (postSnapshot.hasChild("main") && postSnapshot.hasChild("text")) {
                    List<String> mainImageUrls = new ArrayList<>();
                    DataSnapshot mainSnapshot = postSnapshot.child("main");

                    if (mainSnapshot.getValue() instanceof String) {
                        // main 필드가 단일 이미지 URL일 경우
                        String mainImageUrl = mainSnapshot.getValue(String.class);
                        mainImageUrls.add(mainImageUrl);
                    } else {
                        // main 필드가 여러 이미지 URL일 경우
                        for (DataSnapshot imageSnapshot : mainSnapshot.getChildren()) {
                            String imageUrl = imageSnapshot.getValue(String.class);
                            mainImageUrls.add(imageUrl);
                        }
                    }

                    String text = postSnapshot.child("text").getValue(String.class);
                    String memberText = postSnapshot.child("memberText").getValue(String.class);
                    String profileImageUrl = userSnapshot.child("profile").getValue(String.class);
                    String postId = postSnapshot.getKey(); // 실제 postId를 가져옴

                    // 로그 추가
                    Log.d("Menu1", "Profile Image URL: " + profileImageUrl);
                    Log.d("Menu1", "Main Image URLs: " + mainImageUrls.toString());

                    // 아이템 객체 생성 후 리스트에 추가
                    Item item = new Item(userId, text, profileImageUrl, mainImageUrls, memberText, postId);

                    // 이미 저장된 항목인지 확인하여 설정
                    DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(currentUserUid).child("my").child(userId).child("saved_posts").child(postId);
                    savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("Menu1", "Item is saved: " + postId);
                                item.setSaved(true);
                            }
                            itemList.add(item);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Menu1", "Failed to check if item is saved: " + error.getMessage());
                        }
                    });
                }
            }
        }
    }

    // 각 아이템에 저장 버튼을 클릭했을 때 호출되는 메서드
    public void saveItem(String userId, String postId, Item item, ImageButton saveButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Menu1", "사용자가 인증되지 않았습니다.");
            return;
        }

        String currentUserUid = currentUser.getUid();
        DatabaseReference myRef = databaseReference.child("users").child(currentUserUid).child("my").child(userId).child("saved_posts").child(postId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 항목이 이미 저장되어 있음 -> 삭제 로직 추가
                    myRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                saveButton.setEnabled(true);
                                saveButton.setImageResource(R.drawable.white_heart); // 저장되지 않은 아이콘으로 변경
                                item.setSaved(false);
                                Toast.makeText(getContext(), "데이터가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                saveButton.setEnabled(true); // 실패 시 다시 활성화
                                Toast.makeText(getContext(), "데이터 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // 항목이 저장되지 않았으므로 데이터베이스에 저장
                    Map<String, Object> feedData = new HashMap<>();
                    feedData.put("userID", item.getUserId());
                    feedData.put("profile", item.getProfileImageUrl());
                    feedData.put("text", item.getContent());
                    feedData.put("main", item.getMainImageUrls());
                    feedData.put("memberText", item.getMemberText());
                    feedData.put("timestamp", System.currentTimeMillis()); // 타임스탬프 추가

                    myRef.setValue(feedData)
                            .addOnSuccessListener(aVoid -> {
                                saveButton.setEnabled(true); // 성공 시 다시 활성화
                                saveButton.setImageResource(R.drawable.check); // 저장된 아이콘으로 변경
                                item.setSaved(true);
                                Toast.makeText(getContext(), "데이터가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                saveButton.setEnabled(true); // 실패 시 다시 활성화
                                Toast.makeText(getContext(), "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                saveButton.setEnabled(true); // 실패 시 다시 활성화
                Toast.makeText(getContext(), "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // save 메서드 수정: 실제 postId를 사용하도록 수정
    public void save(int position) {
        Item item = itemList.get(position);
        String userId = item.getUserId(); // 게시물 올린 사람의 ID
        String postId = item.getPostId(); // 실제 postId 사용
        ImageButton saveButton = recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.saveButton);
        saveItem(userId, postId, item, saveButton);
    }
}
