package Parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class InconsistencyReporter {
    private static String report = """
            ----------High level Decisions:----------
            For genres, we made a hashmap of genre misspellings and abbreviations to make genres more organized and have less duplicates.
            For actors, we focused on removing actors who lacked a name, and set birth year to null if it didn't exist.
            For casting, we skipped unknown actors 'sa' according to the xml specification, and made sure both the actor and movie existed.
            For movies, we checked for invalid years and missing ids, directors, and bad formatting.
            As a whole, we also worked to filter out duplicate entries.
            """;

    private static class ReportEntry {
        int count = 0;
        List<String> examples = new ArrayList<>();
    }

    private Map<String, ReportEntry> reportMap = new LinkedHashMap<>();

    public void report(String category) {
        ReportEntry entry = reportMap.computeIfAbsent(category, key -> new ReportEntry());
        entry.count++;
    }

    public void writeReport(String filePath) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(new File(filePath))) {
            out.println("XML Inconsistency Report");
            out.println("========================");
            out.println(report);
            out.println();

            if (reportMap.isEmpty()) {
                out.println("No inconsistencies found.");
                return;
            }

            int section = 1;

            for (Map.Entry<String, ReportEntry> mapEntry : reportMap.entrySet()) {
                String category = mapEntry.getKey();
                ReportEntry entry = mapEntry.getValue();

                out.println(section + ". " + category);
                out.println("Count: " + entry.count);

                out.println();
                section++;
            }
        }
    }
}