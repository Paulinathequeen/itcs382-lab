package com.bikeshare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

/** Wires the parts together and starts the program. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        BikeShareService service = new BikeShareService(new Fleet(2), new CsvRideLog(Path.of("rides.csv")));
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        new ConsoleMenu(service, new ReceiptFormatter(), in, System.out).run();
    }
}