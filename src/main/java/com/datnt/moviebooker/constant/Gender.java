package com.datnt.moviebooker.constant;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    UNKNOWN("Không xác định");

    private final String vietnameseLabel;

    Gender(String vietnameseLabel) {
        this.vietnameseLabel = vietnameseLabel;
    }

    @Override
    public String toString() {
        return vietnameseLabel;
    }

    public String getLabel() {
        return vietnameseLabel;
    }
}
