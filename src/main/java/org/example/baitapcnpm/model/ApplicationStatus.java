package org.example.baitapcnpm.model;

public enum ApplicationStatus {
    SUBMITTED("Đã nộp đơn", "badge bg-primary"),
    REVIEWING("Đang xem xét", "badge bg-warning text-dark"),
    ACCEPTED("Trúng tuyển", "badge bg-success"),
    REJECTED("Không phù hợp", "badge bg-danger");

    private final String displayName;
    private final String badgeClass;

    ApplicationStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
