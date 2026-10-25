package com.bikeshare;

/** A ride that has started and is not yet returned. */
public record Rental(String memberId, MemberType memberType, Bike bike, Station from) {
}
