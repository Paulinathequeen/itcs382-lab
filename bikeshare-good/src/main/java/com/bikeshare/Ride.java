package com.bikeshare;

/** A finished ride. */
public record Ride(String memberId, MemberType memberType, String bikeId,
                   Station from, Station to, int minutes, int fee) {
}
