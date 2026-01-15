package report;

import model.FilterKey;

import java.io.PrintWriter;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
/*
* ReportWriter class
* In charge of statistics writing in text files
* */
public class ReportWriter {
    // Applying DRY principle for each filter (GENERAL , HOUR , LOCATION , DAY)
    public static void print(Map<FilterKey, Map<String, DoubleSummaryStatistics>> filteredRecords, String fileName, String title) {
        try (PrintWriter printWriter = new PrintWriter(fileName)) {
            printTitle(printWriter, title);
            for (var entry : filteredRecords.entrySet()) {
                FilterKey filterKey = entry.getKey();
                Map<String, DoubleSummaryStatistics> metrics = entry.getValue();

                printWriter.println("\n- " + filterKey.value() + ":");
                for (var metricEntry : metrics.entrySet()) {
                    String metricName = metricEntry.getKey();
                    DoubleSummaryStatistics stats = metricEntry.getValue();
                    if (stats.getCount() == 0) continue;
                    printWriter.println("\n\t[ " + formatIdName(metricName) + " ]");
                    printWriter.printf(
                            "    \tAvg : %8.2f | Max : %8.2f | Min : %8.2f%n",
                            stats.getAverage(),
                            stats.getMax(),
                            stats.getMin()
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error writing report: " + fileName, e);
        }
    }

    private static void printTitle(PrintWriter buffer, String title) {
        buffer.println("============================================================");
        buffer.println(title);
        buffer.println("============================================================");
    }

    private static String formatIdName(String id) {
        if (id == null || id.isEmpty()) return "";
        String cleaned = id.replaceAll("^_+", "").replace("_", "");
        String spaced = cleaned.replaceAll("(?i)(temp|pressure|speed|direction|humidity|distance|record|of|week)", " $1").trim();
        return spaced.substring(0, 1).toUpperCase() + spaced.substring(1).toLowerCase();
    }
}
