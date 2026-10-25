package com.bikeshare;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

/** Keeps track of which bikes are parked at which station. */
public class Fleet {
    private final Map<Station, Deque<Bike>> parked = new EnumMap<>(Station.class);

    public Fleet(int bikesPerStation) {
        int number = 1;
        for (Station station : Station.values()) {
            Deque<Bike> bikes = new ArrayDeque<>();
            for (int i = 0; i < bikesPerStation; i++) {
                bikes.add(new Bike("B" + number++));
            }
            parked.put(station, bikes);
        }
    }

    public Bike take(Station station) {
        Bike bike = parked.get(station).poll();
        if (bike == null) {
            throw new IllegalStateException("No bike available at " + station);
        }
        return bike;
    }

    public void park(Bike bike, Station station) {
        parked.get(station).add(bike);
    }
}
