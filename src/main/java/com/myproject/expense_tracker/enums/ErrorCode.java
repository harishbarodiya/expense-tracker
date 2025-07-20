package com.myproject.expense_tracker.enums;

public enum ErrorCode {
    EXPENSE_NOT_FOUND("Expense not found."),
    INCOME_NOT_FOUND("Income not found."),
    USER_NOT_FOUND("User not found."),
    UNAUTHORIZED_ACCESS("Unauthorized access."),
    EMAIL_SENDING_FAILED("Failed to send email."),
    ADMIN_DELETE_ADMIN("Admins cannot delete other admins.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}