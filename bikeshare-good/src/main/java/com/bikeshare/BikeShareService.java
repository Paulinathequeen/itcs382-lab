package com.bikeshare;

import java.util.HashMap;
import java.util.Map;

/** The rental rules: who may rent, what a return costs, and what is recorded. */
public class BikeShareService {
    private final Fleet fleet;
    private final RideLog log;
    private final Map<String, Rental> active = new HashMap<>();

    public BikeShareService(Fleet fleet, RideLog log) {
        this.fleet = fleet;
        this.log = log;
    }

    public Bike rent(String memberId, MemberType type, Station station) {
        if (!memberId.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Member ID may only contain letters, digits, _ and -");
        }
        if (active.containsKey(memberId)) {
            throw new IllegalStateException("Member " + memberId + " already has a bike");
        }
        Bike bike = fleet.take(station);
        active.put(memberId, new Rental(memberId, type, bike, station));
        return bike;
    }

    public Ride giveBack(String memberId, Station station, int minutes) {
        Rental rental = active.get(memberId);
        if (rental == null) {
            throw new IllegalStateException("Member " + memberId + " has no bike to return");
        }
        if (minutes <= 0) {
            throw new IllegalArgumentException("Ride time must be a positive number of minutes");
        }
        Ride ride = new Ride(memberId, rental.memberType(), rental.bike().id(), rental.from(),
                station, minutes, rental.memberType().feeFor(minutes));
        log.save(ride);
        fleet.park(rental.bike(), station);
        active.remove(memberId);
        return ride;
    }
}
