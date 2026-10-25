package com.bikeshare;

/** The campus stations where bikes are lent and returned. */
public enum Station {
    LIBRARY,
    CANTEEN,
    DORM;

    public static Station parse(String text) {
        for (Station station : values()) {
            if (station.name().equalsIgnoreCase(text)) {
                return station;
            }
        }
        throw new IllegalArgumentException("Unknown station '" + text + "' (use library, canteen or dorm)");
    }
}
