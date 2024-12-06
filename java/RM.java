package com.esther.perfect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // 추가된 import
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // 추가된 import

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RM extends AppCompatActivity {

    private static final String TAG = "MemberDetailActivity";

    private GridView gridView;
    private AdapterGrid adapter;
    private List<String> imageUrlList;
    private List<String> postIdList;
    private DatabaseReference myRef;

    private ImageView memberProfileImageView;
    private TextView memberUsernameTextView;
    private TextView memberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member);

        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        gridView = findViewById(R.id.member_gridview);
        memberProfileImageView = findViewById(R.id.member_profile);
        memberUsernameTextView = findViewById(R.id.member_username);
        memberTextView = findViewById(R.id.member_text);

        imageUrlList = new ArrayList<>();
        postIdList = new ArrayList<>();
        adapter = new AdapterGrid(this, imageUrlList);
        gridView.setAdapter(adapter);

        myRef = FirebaseDatabase.getInstance().getReference().child("member").child("RM");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot: " + dataSnapshot.toString());

                String profileImage = dataSnapshot.child("profile").getValue(String.class);
                String username = dataSnapshot.child("userID").getValue(String.class);
                String memberText = dataSnapshot.child("memberText").getValue(String.class);

                if (profileImage != null) {
                    Glide.with(RM.this)
                            .load(profileImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into(memberProfileImageView);
                }
                if (username != null) {
                    memberUsernameTextView.setText(username);
                }
                if (memberText != null) {
                    memberTextView.setText(memberText);
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        int nodeNumber = Integer.parseInt(snapshot.getKey());
                        DataSnapshot mainSnapshot = snapshot.child("main");
                        String imageUrl = null;

                        // main 필드가 단일 이미지 URL인지 여러 이미지 URL인지 확인
                        if (mainSnapshot.getValue() instanceof String) {
                            imageUrl = mainSnapshot.getValue(String.class);
                        } else {
                            // main 필드가 여러 이미지 URL일 경우 첫 번째 이미지 URL 가져오기
                            for (DataSnapshot imageSnapshot : mainSnapshot.getChildren()) {
                                imageUrl = imageSnapshot.getValue(String.class);
                                break; // 첫 번째 이미지 URL만 가져옴
                            }
                        }

                        if (imageUrl != null) {
                            imageUrlList.add(imageUrl);
                            postIdList.add(snapshot.getKey());
                        }
                    } catch (NumberFormatException e) {
                        // 숫자 노드가 아닐 경우 무시
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error: " + databaseError.getMessage());
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String clickedPostId = postIdList.get(position);
            Intent intent = new Intent(RM.this, MemberDetailActivity.class);
            intent.putExtra("userID", "RM");
            intent.putExtra("postID", clickedPostId);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로 가기 동작
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
