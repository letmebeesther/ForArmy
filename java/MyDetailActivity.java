package com.esther.perfect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDetailActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String userID;
    private String postID;

    // 클래스 필드로 정의
    private String profileImageUrl;
    private List<String> mainImageUrls;
    private String text;
    private String userIDValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_detail);

        // 툴바 초기화
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Firebase Realtime Database의 루트 참조
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 레이아웃 요소들 참조
        TextView userIdTextView = findViewById(R.id.user_id_text_view);
        TextView contentTextView = findViewById(R.id.content_text_view);
        ImageView profileImageView = findViewById(R.id.profile_image_view);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ImageView saveButton = findViewById(R.id.saveButton);

        // 전달된 데이터 가져오기
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        postID = intent.getStringExtra("postID");

        // 현재 로그인한 사용자의 UID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "사용자가 인증되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String currentUserUid = currentUser.getUid();

        // userID와 postID를 사용하여 해당 사용자의 데이터를 가져옵니다.
        databaseReference.child("users").child(currentUserUid).child("my").child(userID).child("saved_posts").child(postID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 데이터 가져오기
                        profileImageUrl = dataSnapshot.child("profile").getValue(String.class);
                        mainImageUrls = new ArrayList<>();
                        DataSnapshot mainSnapshot = dataSnapshot.child("main");

                        if (mainSnapshot.getValue() instanceof String) {
                            mainImageUrls.add(mainSnapshot.getValue(String.class));
                        } else {
                            for (DataSnapshot imageSnapshot : mainSnapshot.getChildren()) {
                                mainImageUrls.add(imageSnapshot.getValue(String.class));
                            }
                        }

                        text = dataSnapshot.child("text").getValue(String.class);
                        userIDValue = dataSnapshot.child("userID").getValue(String.class);

                        // 가져온 데이터를 화면에 표시
                        userIdTextView.setText(userIDValue);
                        contentTextView.setText(text);

                        // 프로필 이미지 로드
                        Glide.with(MyDetailActivity.this)
                                .load(profileImageUrl)
                                .into(profileImageView);

                        // ViewPager2와 TabLayout 설정
                        AdapterViewPager adapterViewPager = new AdapterViewPager(MyDetailActivity.this, mainImageUrls);
                        viewPager.setAdapter(adapterViewPager);

                        new TabLayoutMediator(tabLayout, viewPager,
                                (tab, position) -> {
                                    // Customizing tabs if needed
                                }).attach();

                        // 저장 여부 확인 및 저장 버튼 설정
                        if (dataSnapshot.exists()) {
                            saveButton.setImageResource(R.drawable.check); // 저장된 아이콘 설정
                        } else {
                            saveButton.setImageResource(R.drawable.white_heart); // 저장 아이콘 설정
                        }

                        // 저장 버튼 클릭 리스너 설정
                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toggleSaveState(currentUserUid);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 데이터 읽기가 취소되었을 때 호출되는 콜백
                        Toast.makeText(MyDetailActivity.this, "Failed to read data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 저장 상태를 전환하는 메서드
    private void toggleSaveState(String currentUserUid) {
        DatabaseReference postRef = databaseReference.child("users").child(currentUserUid).child("my").child(userID).child("saved_posts").child(postID);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 이미 저장된 상태이면 삭제
                    postRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MyDetailActivity.this, "게시물이 저장 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                ImageView saveButton = findViewById(R.id.saveButton);
                                saveButton.setImageResource(R.drawable.white_heart); // 저장되지 않은 아이콘으로 변경
                            })
                            .addOnFailureListener(e -> Toast.makeText(MyDetailActivity.this, "게시물 저장 취소에 실패했습니다.", Toast.LENGTH_SHORT).show());
                } else {
                    // 저장되지 않은 상태이면 저장
                    Map<String, Object> postData = new HashMap<>();
                    postData.put("userID", userIDValue);
                    postData.put("profile", profileImageUrl);
                    postData.put("main", mainImageUrls);
                    postData.put("text", text);

                    postRef.setValue(postData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MyDetailActivity.this, "게시물이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                ImageView saveButton = findViewById(R.id.saveButton);
                                saveButton.setImageResource(R.drawable.check); // 저장된 아이콘으로 변경
                            })
                            .addOnFailureListener(e -> Toast.makeText(MyDetailActivity.this, "게시물 저장에 실패했습니다.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyDetailActivity.this, "데이터 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 새로고침을 알리며 종료
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
