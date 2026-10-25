package com.bikeshare;

/** Turns a finished ride into receipt text. */
public class ReceiptFormatter {
    public String format(Ride ride) {
        return String.format("--- Receipt ---%nBike:    %s%nMinutes: %d%nFee:     %d baht%n---------------",
                ride.bikeId(), ride.minutes(), ride.fee());
    }
}
