Task 4 A

Here is the program. It lives in one class on purpose, so the design problems are easy to point at.

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>bikeshare</groupId>
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
                            <mainClass>bikeshare.BikeShare</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**src/main/java/bikeshare/BikeShare.java**

```java
package bikeshare;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class BikeShare {
    public static final int STUDENT = 0;
    public static final int STAFF = 1;
    public static final String[] STATIONS = {"Library", "Canteen", "Dorm"};

    public static int[] bikeAt = {0, 0, 1, 1, 2, 2};   // station index, -1 = rented out
    public static String[] riderOf = new String[6];
    public static int[] typeOf = new int[6];            // STUDENT or STAFF

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Commands: rent <id> <student|staff> <station>, "
                    + "return <id> <station> <minutes>, quit");
            while (in.hasNextLine()) {
                String[] w = in.nextLine().trim().split("\\s+");
                if (w[0].equals("quit")) {
                    break;
                } else if (w[0].equals("rent")) {
                    run(w, true);
                } else if (w[0].equals("return")) {
                    run(w, false);
                } else {
                    System.out.println("Unknown command");
                }
            }
        }
    }

    public static void run(String[] w, boolean renting) {
        if (w.length != 4) {
            System.out.println("Wrong number of arguments");
            return;
        }
        int station = stationIndex(renting ? w[3] : w[2]);
        if (station < 0) {
            System.out.println("Unknown station");
            return;
        }
        if (renting) {
            if (!w[2].equals("student") && !w[2].equals("staff")) {
                System.out.println("Member type must be student or staff");
                return;
            }
            for (int b = 0; b < bikeAt.length; b++) {
                if (bikeAt[b] == station) {
                    bikeAt[b] = -1;
                    riderOf[b] = w[1];
                    typeOf[b] = w[2].equals("staff") ? STAFF : STUDENT;
                    System.out.println("Bike " + (b + 1) + " rented");
                    return;
                }
            }
            System.out.println("No bike available at " + STATIONS[station]);
        } else {
            int minutes;
            try {
                minutes = Integer.parseInt(w[3]);
            } catch (NumberFormatException e) {
                System.out.println("Minutes must be a whole number");
                return;
            }
            if (minutes < 0) {
                System.out.println("Minutes cannot be negative");
                return;
            }
            for (int b = 0; b < bikeAt.length; b++) {
                if (bikeAt[b] == -1 && w[1].equals(riderOf[b])) {
                    int fee = minutes * (typeOf[b] == STAFF ? 3 : 2);
                    System.out.println("Receipt: bike " + (b + 1) + ", " + minutes
                            + " min, fee " + fee + " baht");
                    save(b + 1, w[1], minutes, fee);
                    bikeAt[b] = station;
                    riderOf[b] = null;
                    return;
                }
            }
            System.out.println("No active rental for member " + w[1]);
        }
    }

    public static int stationIndex(String name) {
        for (int i = 0; i < STATIONS.length; i++) {
            if (STATIONS[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public static void save(int bike, String member, int minutes, int fee) {
        try (FileWriter writer = new FileWriter("rides.csv", true)) {
            writer.write(bike + "," + member + "," + minutes + "," + fee + "\n");
        } catch (IOException e) {
            System.out.println("Could not write rides.csv: " + e.getMessage());
        }
    }
}
```

**Build and run**

```
mvn package
java -jar target/bikeshare-1.0.jar
```

Example session:

```
rent s123 student library
return s123 dorm 15
quit
```

The poor design is deliberate:

- **One class does everything:** `BikeShare` holds the menu loop, the fee rules, the bike records and the file saving.
- **Public static shared data:** `bikeAt`, `riderOf` and `typeOf` are public static arrays, and any code can change them.
- **Int codes:** member types are `STUDENT = 0` and `STAFF = 1`, and `bikeAt` uses -1 to mean "rented out".
- **Boolean flag:** `run(w, renting)` either rents or returns a bike depending on the flag.

The code itself is tidy: the `Scanner` and `FileWriter` are closed with try-with-resources, `NumberFormatException` and `IOException` are handled, and nothing is unused.