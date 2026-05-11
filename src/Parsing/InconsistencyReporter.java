package Parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class InconsistencyReporter {
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