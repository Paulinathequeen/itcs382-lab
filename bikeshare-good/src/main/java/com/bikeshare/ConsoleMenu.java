package com.bikeshare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;

/** Reads commands, calls the service and prints the results. */
public class ConsoleMenu {
    private static final String HELP = """
            Commands:
              rent <memberId> <student|staff> <library|canteen|dorm>
              return <memberId> <library|canteen|dorm> <minutes>
              quit""";

    private final BikeShareService service;
    private final ReceiptFormatter receipts;
    private final BufferedReader in;
    private final PrintStream out;

    public ConsoleMenu(BikeShareService service, ReceiptFormatter receipts, BufferedReader in, PrintStream out) {
        this.service = service;
        this.receipts = receipts;
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        out.println(HELP);
        String line;
        while ((line = in.readLine()) != null && !line.trim().equalsIgnoreCase("quit")) {
            if (!line.isBlank()) {
                handle(line.trim().split("\\s+"));
            }
        }
    }

    private void handle(String[] words) {
        try {
            switch (words[0].toLowerCase()) {
                case "rent" -> rent(words);
                case "return" -> giveBack(words);
                default -> out.println("Unknown command. " + HELP);
            }
        } catch (IllegalArgumentException | IllegalStateException | UncheckedIOException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void rent(String[] w) {
        requireWords(w, 4, "rent <memberId> <student|staff> <station>");
        Bike bike = service.rent(w[1], MemberType.parse(w[2]), Station.parse(w[3]));
        out.println("Member " + w[1] + " rented bike " + bike.id() + " at " + Station.parse(w[3]));
    }

    private void giveBack(String[] w) {
        requireWords(w, 4, "return <memberId> <station> <minutes>");
        Ride ride = service.giveBack(w[1], Station.parse(w[2]), parseMinutes(w[3]));
        out.println(receipts.format(ride));
    }

    private static int parseMinutes(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Minutes must be a whole number");
        }
    }

    private static void requireWords(String[] words, int count, String usage) {
        if (words.length != count) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }
}
