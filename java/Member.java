package com.esther.perfect;

public class Member {
    private String profileImage;
    private String userId;
    private String memberText;
    private String key;  // Firebase 키를 저장하는 필드

    public Member(String profileImage, String userId, String memberText) {
        this.profileImage = profileImage;
        this.userId = userId;
        this.memberText = memberText;
        this.key = key;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getMemberText() {
        return memberText;
    }

    public String getKey() {
        return key;
    }
}
