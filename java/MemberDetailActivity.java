package com.esther.perfect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class MemberDetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private ImageView profileImageView;
    private TextView userIdTextView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView contentTextView;
    private ImageButton saveButton;

    private String profileImage;
    private String username;
    private List<String> mainImages;
    private String contentText;
    private String memberText;
    private String userId; // 추가된 필드
    private String postId; // 추가된 필드

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

        profileImageView = findViewById(R.id.profile_image_view);
        userIdTextView = findViewById(R.id.user_id_text_view);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        contentTextView = findViewById(R.id.content_text_view);
        saveButton = findViewById(R.id.saveButton);

        // 인텐트로부터 클릭된 항목의 ID를 가져옵니다.
        Intent intent = getIntent();
        userId = intent.getStringExtra("userID");
        postId = intent.getStringExtra("postID");

        if (userId != null && postId != null) {
            loadDetailData(userId, postId);
        } else {
            Log.e(TAG, "userID or postID is null");
            finish(); // userID나 postID가 null일 경우 액티비티를 종료합니다.
        }
    }

    private void loadDetailData(String userId, String postId) {
        DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference().child("member").child(userId);
        DatabaseReference postRef = memberRef.child(postId);

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileImage = dataSnapshot.child("profile").getValue(String.class);
                username = dataSnapshot.child("userID").getValue(String.class);
                memberText = dataSnapshot.child("memberText").getValue(String.class);

                if (profileImage != null) {
                    Glide.with(MemberDetailActivity.this)
                            .load(profileImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                }
                if (username != null) {
                    userIdTextView.setText(username);
                }

                // post 데이터를 가져오기 위해 두 번째 데이터베이스 참조를 사용합니다.
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                        contentText = postSnapshot.child("text").getValue(String.class);
                        mainImages = new ArrayList<>();
                        for (DataSnapshot mainSnapshot : postSnapshot.child("main").getChildren()) {
                            String imageUrl = mainSnapshot.getValue(String.class);
                            if (imageUrl != null) {
                                mainImages.add(imageUrl);
                            }
                        }

                        if (mainImages.isEmpty() && postSnapshot.child("main").getValue(String.class) != null) {
                            mainImages.add(postSnapshot.child("main").getValue(String.class));
                        }

                        AdapterViewPager adapter = new AdapterViewPager(MemberDetailActivity.this, mainImages);
                        viewPager.setAdapter(adapter);
                        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

                        if (contentText != null) {
                            contentTextView.setText(contentText);
                        }

                        checkIfPostSaved(userId, postId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error: " + databaseError.getMessage());
            }
        });
    }

    private void checkIfPostSaved(String userId, String postId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "사용자가 인증되지 않았습니다.");
            return;
        }

        String currentUserUid = currentUser.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(currentUserUid).child("my")
                .child(userId).child("saved_posts").child(postId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    saveButton.setImageResource(R.drawable.check); // 저장된 아이콘 설정
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removePost(myRef);
                        }
                    });
                } else {
                    saveButton.setImageResource(R.drawable.white_heart); // 저장되지 않은 아이콘 설정
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savePost(myRef);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error: " + databaseError.getMessage());
            }
        });
    }

    private void savePost(DatabaseReference myRef) {
        // 저장할 데이터 준비
        Map<String, Object> postData = new HashMap<>();
        postData.put("profile", profileImage);
        postData.put("text", contentText);
        postData.put("main", mainImages);
        postData.put("userID", username);
        postData.put("memberText", memberText); // memberText 추가

        myRef.setValue(postData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveButton.setImageResource(R.drawable.check); // 저장된 아이콘 설정
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removePost(myRef);
                    }
                });
            } else {
                Log.e(TAG, "Failed to save post");
            }
        });
    }

    private void removePost(DatabaseReference myRef) {
        myRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveButton.setImageResource(R.drawable.white_heart); // 저장되지 않은 아이콘 설정
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePost(myRef);
                    }
                });
            } else {
                Log.e(TAG, "Failed to remove post");
            }
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
