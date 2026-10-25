package com.bikeshare;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Appends finished rides to a CSV file. */
public class CsvRideLog implements RideLog {
    private static final String HEADER = "memberId,memberType,bike,from,to,minutes,fee";

    private final Path file;

    public CsvRideLog(Path file) {
        this.file = file;
    }

    @Override
    public void save(Ride ride) {
        String line = String.join(",", ride.memberId(), ride.memberType().name(), ride.bikeId(),
                ride.from().name(), ride.to().name(), String.valueOf(ride.minutes()), String.valueOf(ride.fee()));
        String text = (Files.exists(file) ? "" : HEADER + System.lineSeparator()) + line + System.lineSeparator();
        try {
            Files.writeString(file, text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
    }
}
