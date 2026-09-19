package org.example.baitapcnpm.model;

public enum Role {
    ROLE_CANDIDATE("Ứng viên"),
    ROLE_RECRUITER("Nhà tuyển dụng"),
    ROLE_ADMIN("Quản trị viên");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
