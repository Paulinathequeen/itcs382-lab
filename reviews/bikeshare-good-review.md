The Maven project is written: `pom.xml` plus 12 Java files in `com.bikeshare`. I couldn't compile or run it here because the sandbox has no JDK, so it is untested.

**Build and run:**
```
mvn package
java -jar target/bikeshare-1.0.jar
```

**Commands:**
```
rent <memberId> <student|staff> <library|canteen|dorm>
return <memberId> <library|canteen|dorm> <minutes>
quit
```

**Classes:**
- **`MemberType` and `Station`:** the enums. `MemberType` holds the rate (2 or 3 baht) and computes the fee.
- **`Bike`, `Rental`, `Ride`:** small records for the data. `Rental` is a ride in progress and `Ride` is a finished one.
- **`Fleet`:** which bikes are parked at which station, two each at the start.
- **`BikeShareService`:** the rules. It checks the member ID, allows one active rental per member, builds the `Ride` and saves it.
- **`RideLog` and `CsvRideLog`:** the saving interface and its CSV version. `CsvRideLog` appends to `rides.csv` and writes a header on first use.
- **`ReceiptFormatter`:** the receipt text with bike, minutes and fee.
- **`ConsoleMenu`:** reads commands, parses them and prints messages. It is the only class that touches the keyboard or screen, and it holds no rules.
- **`Main`:** creates the objects and starts the menu.

**Behaviours to know:**
- A return is saved to the file before the bike is parked again, so a failed write leaves the rental intact.
- Bikes can be returned to any station with no capacity limit, as the spec says.
- A member can't hold two bikes at once.
- Ride time must be a positive whole number of minutes.
- Member IDs may only contain letters, digits, `_` and `-`, so they can't break the CSV.

Here is everything, with each file's path above its code. As before, I couldn't compile it here because the sandbox has no JDK.

**pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.bikeshare</groupId>
  <artifactId>bikeshare</artifactId>
  <version>1.0</version>
  <packaging>jar</packaging>

  <properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.3.0</version>
        <configuration>
          <archive>
            <manifest>
              <mainClass>com.bikeshare.Main</mainClass>
            </manifest>
          </archive>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

**src/main/java/com/bikeshare/MemberType.java**
```java
package com.bikeshare;

/** Kind of member and the price it pays per minute of riding. */
public enum MemberType {
    STUDENT(2),
    STAFF(3);

    private final int bahtPerMinute;

    MemberType(int bahtPerMinute) {
        this.bahtPerMinute = bahtPerMinute;
    }

    public int feeFor(int minutes) {
        return bahtPerMinute * minutes;
    }

    public static MemberType parse(String text) {
        for (MemberType type : values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown member type '" + text + "' (use student or staff)");
    }
}
```

**src/main/java/com/bikeshare/Station.java**
```java
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
```

**src/main/java/com/bikeshare/Bike.java**
```java
package com.bikeshare;

/** A single bike, identified by its ID. */
public record Bike(String id) {
}
```

**src/main/java/com/bikeshare/Rental.java**
```java
package com.bikeshare;

/** A ride that has started and is not yet returned. */
public record Rental(String memberId, MemberType memberType, Bike bike, Station from) {
}
```

**src/main/java/com/bikeshare/Ride.java**
```java
package com.bikeshare;

/** A finished ride. */
public record Ride(String memberId, MemberType memberType, String bikeId,
                   Station from, Station to, int minutes, int fee) {
}
```

**src/main/java/com/bikeshare/Fleet.java**
```java
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
```

**src/main/java/com/bikeshare/RideLog.java**
```java
package com.bikeshare;

/** Where finished rides are saved. */
public interface RideLog {
    void save(Ride ride);
}
```

**src/main/java/com/bikeshare/CsvRideLog.java**
```java
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
```

**src/main/java/com/bikeshare/BikeShareService.java**
```java
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
```

**src/main/java/com/bikeshare/ReceiptFormatter.java**
```java
package com.bikeshare;

/** Turns a finished ride into receipt text. */
public class ReceiptFormatter {
    public String format(Ride ride) {
        return String.format("--- Receipt ---%nBike:    %s%nMinutes: %d%nFee:     %d baht%n---------------",
                ride.bikeId(), ride.minutes(), ride.fee());
    }
}
```

**src/main/java/com/bikeshare/ConsoleMenu.java**
```java
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
```

**src/main/java/com/bikeshare/Main.java**
```java
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
```

**Build and run:**
```
mvn package
java -jar target/bikeshare-1.0.jar
```

**Example session:**
```
rent s1 student library
return s1 canteen 10
quit
```
This prints a receipt with bike B1, 10 minutes and a fee of 20 baht, and appends the ride to `rides.csv`.