package org.sopt.server.model;

public enum Category {
    FREE, QUESTION, INFO;

    public static Category fromNumber(int number) {
        return switch (number) {
            case 1 -> FREE;
            case 2 -> QUESTION;
            case 3 -> INFO;
            default -> throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        };
    }
}
