package org.example.baitapcnpm.model;

public enum JobStatus {
    PENDING_APPROVAL("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Bị từ chối"),
    CLOSED("Đã đóng");

    private final String displayName;

    JobStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
