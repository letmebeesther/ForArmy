package com.esther.perfect;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterMembers extends RecyclerView.Adapter<AdapterMembers.ViewHolder> {
    private List<Member> members;
    private Fragment fragment;

    // userID와 Activity 클래스를 매핑하는 맵
    private Map<String, Class<?>> activityMap;

    AdapterMembers(Fragment fragment, List<Member> members) {
        this.fragment = fragment;
        this.members = members;

        // userID와 Activity 클래스를 매핑
        activityMap = new HashMap<>();
        activityMap.put("JK", JK.class);
        activityMap.put("RM", RM.class);
        activityMap.put("V", V.class);
        activityMap.put("jimin", jimin.class);
        activityMap.put("jin", jin.class);
        activityMap.put("suga", suga.class);
        activityMap.put("jhope", jhope.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.memberName.setText(member.getUserId());
        holder.memberText.setText(member.getMemberText());

        Glide.with(holder.itemView)
                .load(member.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> activityClass = activityMap.get(member.getUserId());
                if (activityClass != null) {
                    Intent intent = new Intent(fragment.getActivity(), activityClass);
                    fragment.startActivity(intent);
                } else {
                    // 기본 동작 또는 오류 처리
                    // 예: 기본 Activity로 이동하거나, 사용자에게 알림 표시
                    // Intent intent = new Intent(fragment.getActivity(), DefaultActivity.class);
                    // fragment.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView memberName;
        TextView memberText;

        ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.member_profileImage);
            memberName = itemView.findViewById(R.id.member_name);
            memberText = itemView.findViewById(R.id.member_text);
        }
    }
}
