# Lab Report, Code Quality and Design Quality

Repository URL: https://github.com/Paulinathequeen/itcs382-lab.git

## Tools

| Item | Version or name |
| --- | --- |
| JDK | OpenJDK 25.0.4.1 (Temurin) |
| Maven | Apache Maven 3.10.0 |
| IDE | Visual Studio Code 1.143.1 |
| SonarQube for IDE | 6.2.0 |
| AI tool and exact model | Claude Sonnet 5.5 |

## Task 1. SonarQube on the starter code

Commit of the starter, before any change: 990d80e

Table 1. SonarQube issues in the starter code

| No. | Rule | File:line | Message |
| --- | --- | --- | --- |
| 1 | java:S106 | Main.java:9 | Replace this use of System.out by a logger. |
| 2 | java:S106 | Main.java:15 | Replace this use of System.out by a logger. |
| 3 | java:S106 | Main.java:17 | Replace this use of System.out by a logger. |
| 4 | java:S106 | Main.java:22 | Replace this use of System.out by a logger. |
| 5 | java:S2143 | ParcelService.java:1 | Use the "java.time" API for date and time. |
| 6 | java:S1128 | ParcelService.java:6 | Remove this unused import 'java.util.Date'. |
| 7 | java:S1068 | ParcelService.java:12 | Remove this unused "maxParcels" private field. |
| 8 | java:S106 | ParcelService.java:30 | Replace this use of System.out by a logger. |
| 9 | java:S4973 | ParcelService.java:42 | Strings and Boxed types should be compared using "equals()". |
| 10 | java:S106 | ParcelService.java:67 | Replace this use of System.out by a logger. |
| 11 | java:S106 | ParcelService.java:69 | Replace this use of System.out by a logger. |
| 12 | java:S2095 | ParcelService.java:76 | Use try-with-resources or close this "FileWriter" in a "finally" clause. |
| 13 | java:S108 | ParcelService.java:80 | Remove this block of code, fill it in, or add a comment explaining why it is empty. |

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 25 October 2025 |
| AI tool and exact model | Claude Sonnet 5.5 |
| Commit reviewed | 990d80e |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Attached the files |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | Strings compared using == | Both | java:S4973 | yes, causes pickup to fail. |
| 2 | ParcelService.java:76 | FileWriter is never closed | Both | java:S2095 | yes, resource leak. |
| 3 | ParcelService.java:80 | Empty catch block | Both | java:S108 | yes, hides errors silently. |
| 4 | ParcelService.java:6 | Unused import 'java.util.Date' | Both | java:S1128 | yes, dead code. |
| 5 | ParcelService.java:12 | Unused field 'maxParcels' | Both | java:S1068 | yes, field is never read. |
| 6 | Main.java:9 | Replace System.out by a logger | SonarQube | java:S106 | no, it is a CLI app, so console output is intended. |
| 7 | ParcelService.java:9 | Weak class cohesion (handles fees, lockers, SMS, files) | AI | Class cohesion | yes, the class has too many responsibilities. |
| 8 | ParcelService.java:28 | Content coupling (modifies Parcel's public status) | AI | Content coupling | yes, breaks encapsulation. |

2.1 Name two problems that only the AI review found.

The AI found design-level issues that SonarQube missed, such as *weak class cohesion* in `ParcelService` (it handles too many unrelated jobs like fees, files, and SMS) and *content coupling* (it directly modifies the public `status` field of the `Parcel` class).

2.2 Name one problem that only SonarQube found, or write none.

SonarQube found that the `java.time` API should be used instead of `java.util.Date` (Rule S2143), which the AI did not explicitly mention.

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

Most AI findings were correct when checked against the code (e.g., verifying the `==` comparison on line 42). However, the AI noted that the `Scanner` in `Main.java` is never closed. While technically true, it is too pedantic for a short-lived CLI application, as closing `System.in` is generally not required here.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | | |

Number of SonarQube issues that remain after your fixes:

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| | | |
| | | |

Output of the program after your fixes, for the commands `store P1 6501 2`, `pickup P1 5` and `staff`, and the content of parcels.csv:

```text

```

## Task 4. AI-generated poor and good designs

| Item | bikeshare-poor | bikeshare-good |
| --- | --- | --- |
| Date | | |
| AI tool and exact model | | |
| Prompt (B or C as given, or your changed prompt in full) | | |
| Builds with mvn package (yes or no) | | |
| Number of classes, interfaces, enums and records | | |
| SonarQube issues, total | | |
| SonarQube issues, by rule | | |
| AI review findings in Check 1 | | |
| AI review findings in Check 2 | | |
| Review output file in the reviews folder | | |

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

5.2 When should you not trust the AI review? Give an example from this lab.

5.3 What will you do differently in Project Phase 2 after this lab?

## AI usage declaration

| AI tool and exact model | Purpose | Files or parts that it produced or changed | How you checked its output |
| --- | --- | --- | --- |
| | | | |
