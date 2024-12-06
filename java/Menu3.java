package com.esther.perfect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Menu3 extends Fragment {

    private static final int REQUEST_CODE_DETAIL = 1; // 상수 정의

    private GridView gridView;
    private AdapterGrid adapter;
    private List<String> imageUrlList;
    private List<String> userIdList;
    private List<String> postIdList;
    private DatabaseReference usersRef;

    private static final String TAG = "Menu3"; // 로그 태그 추가

    private ImageView profileImageView;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu3, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        profileImageView = rootView.findViewById(R.id.profile);
        usernameTextView = rootView.findViewById(R.id.username);
        gridView = rootView.findViewById(R.id.posts_gridview);

        imageUrlList = new ArrayList<>();
        userIdList = new ArrayList<>();
        postIdList = new ArrayList<>();
        adapter = new AdapterGrid(getContext(), imageUrlList);
        gridView.setAdapter(adapter);

        loadProfileData();
        loadPostsData();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedUserId = userIdList.get(position);
                String clickedPostId = postIdList.get(position);
                Intent intent = new Intent(getActivity(), MyDetailActivity.class);
                intent.putExtra("userID", clickedUserId);
                intent.putExtra("postID", clickedPostId);
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
        loadPostsData();
    }

    private void loadProfileData() {
        // Firebase Auth에서 현재 사용자 UID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "사용자가 인증되지 않았습니다.");
            return;
        }

        String currentUserUid = currentUser.getUid();

        // 데이터베이스 참조 경로: users/{currentUserUid}
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImageUrl = dataSnapshot.child("profile").getValue(String.class);
                String nickname = dataSnapshot.child("nickname").getValue(String.class);

                if (profileImageUrl != null) {
                    Glide.with(getContext())
                            .load(profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                }
                if (nickname != null) {
                    usernameTextView.setText(nickname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "프로필 데이터 읽기 취소됨: " + databaseError.getMessage());
            }
        });
    }

    private void loadPostsData() {
        // Firebase Auth에서 현재 사용자 UID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "사용자가 인증되지 않았습니다.");
            return;
        }

        String currentUserUid = currentUser.getUid();

        // 데이터베이스 참조 경로: users/{currentUserUid}/my
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("my");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 초기화
                imageUrlList.clear();
                userIdList.clear();
                postIdList.clear();

                // 각 게시물 올린 사람의 ID를 반복
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    // 각 사용자의 saved_posts 반복
                    for (DataSnapshot postSnapshot : userSnapshot.child("saved_posts").getChildren()) {
                        String postId = postSnapshot.getKey();
                        String imageUrl = null;

                        // main 필드가 단일 이미지 URL인지 여러 이미지 URL인지 확인
                        DataSnapshot mainSnapshot = postSnapshot.child("main");
                        if (mainSnapshot.exists()) {
                            if (mainSnapshot.getValue() instanceof String) {
                                imageUrl = mainSnapshot.getValue(String.class);
                            } else {
                                // main 필드가 여러 이미지 URL일 경우 첫 번째 이미지 URL 가져오기
                                for (DataSnapshot imageSnapshot : mainSnapshot.getChildren()) {
                                    imageUrl = imageSnapshot.getValue(String.class);
                                    break; // 첫 번째 이미지 URL만 가져옴
                                }
                            }

                            // 로그 추가
                            Log.d(TAG, "UserID: " + userId + " PostID: " + postId + " ImageURL: " + imageUrl);

                            if (imageUrl != null && postId != null) {
                                imageUrlList.add(imageUrl);
                                userIdList.add(userId);
                                postIdList.add(postId);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기가 취소되었을 때 호출되는 콜백
                Log.e(TAG, "데이터 읽기 취소됨: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == AppCompatActivity.RESULT_OK) {
            loadPostsData(); // 데이터를 다시 불러옴
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // 로그아웃 처리
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), login.class));
            getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.setting) {
            // 프로필 설정 화면으로 이동
            Intent intent = new Intent(getActivity(), profile_setting.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
