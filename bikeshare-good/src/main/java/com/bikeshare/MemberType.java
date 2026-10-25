package com.bikeshare;

/** Kind of member and the price it pays per minute of riding. */
public enum MemberType {
    STUDENT(2),
    STAFF(3);

    private final int bahtPerMinute;

    MemberType(int bahtPerMinute) {
        this.bahtPerMinute = bahtPerMinute;
    }

    public int feeFor(int minutes) {
        return bahtPerMinute * minutes;
    }

    public static MemberType parse(String text) {
        for (MemberType type : values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown member type '" + text + "' (use student or staff)");
    }
}
