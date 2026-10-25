package com.bikeshare;

/** Where finished rides are saved. */
public interface RideLog {
    void save(Ride ride);
}
