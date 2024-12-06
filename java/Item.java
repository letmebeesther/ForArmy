package com.esther.perfect;

import java.util.List;

public class Item {

    private String userId;
    private String content;
    private String profileImageUrl;
    private List<String> mainImageUrls; // 수정된 필드
    private String memberText;
    private String postId;
    private boolean saved;

    public Item(String userId, String content, String profileImageUrl, List<String> mainImageUrls, String memberText, String postId) {
        this.userId = userId;
        this.content = content;
        this.profileImageUrl = profileImageUrl;
        this.mainImageUrls = mainImageUrls;
        this.memberText = memberText;
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public List<String> getMainImageUrls() {
        return mainImageUrls;
    }

    public String getMemberText() {
        return memberText;
    }

    public String getPostId() {
        return postId;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
