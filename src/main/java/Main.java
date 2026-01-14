import model.WeatherDataset;
import model.FieldMetadata;
/*
    Using 'Jackson' library for easy JSON reading,
* */
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    // One single instance from the ObjectMapper - SINGLETON
    record dayRecord(String date , String dayOfWeek) {};
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        String fileName = "src/main/resources/WeatherStations.json";
        long start = System.nanoTime();
        List<String> fields = readFields(fileName);
        List<Map<String, Object>> records = readRecords(fileName, fields);
        long end = System.nanoTime();
        System.out.printf(
                "%-25s : %8.2f ms%n",
                "Reading time",
                (end - start) / 1_000_000.0
        );
        // General report
        timed("General statistics", () -> getGeneralStatistics(fields, records));
        // Date filter report
        Map<dayRecord, List<Map<String, Object>>> byDay = records.stream()
                        .filter(map -> map.get("time") != null && map.get("dayofweek") != null)
                        .collect(Collectors.groupingBy(
                                key -> new dayRecord(
                                        key.get("time").toString().substring(0 , 10),
                                        key.get("dayofweek").toString())
                        ));
        timed("Day statistics" , () -> getStatisticsBy(
                fields,
                byDay,
                format -> format.date() + " " + format.dayOfWeek(),
                "Statistics - Filtered by Date",
                "outputs/dayStatistics.txt"
        ));
        Map<String , List<Map<String, Object>>> byHour = records.stream()
                .filter(map -> map.get("time") != null)
                .collect(Collectors.groupingBy(
                        key -> key.get("time").toString().substring(11 , 16)
                ));
        timed("Hour statistics" , () -> getStatisticsBy(
                fields,
                byHour,
                format -> format,
                "Statistics - Filtered by Hour",
                "outputs/hourStatistics.txt"
        ));

        Map<String , List<Map<String, Object>>> byLocation = records.stream()
                        .filter(map -> map.get("name") != null)
                        .collect(Collectors.groupingBy(
                                        key -> key.get("name").toString()));
        timed("Location statistics" , () -> getStatisticsBy(
                fields,
                byLocation,
                format -> format,
                "Statistics - Filtered by Location",
                "outputs/locationStatistics.txt"
        ));
    }

    private static void timed(String title, Runnable task){
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        System.out.printf(
                "%-25s : %8.2f ms%n",
                title,
                (endTime - startTime) / 1_000_000.0
        );
    }

    private static String formatIdName(String id) {
        if (id == null || id.isEmpty()) return "";
        String cleaned = id.replaceAll("^_+", "").replace("_", "");
        String spaced = cleaned.replaceAll("(?i)(temp|pressure|speed|direction|humidity|distance|record|of|week)", " $1").trim();
        return spaced.substring(0, 1).toUpperCase() + spaced.substring(1).toLowerCase();
    }

    public static List<String> readFields(String fileName) {
        try {
            WeatherDataset challengeResponse = objectMapper.readValue(new File(fileName), WeatherDataset.class);
            return challengeResponse.getFields().stream().map(FieldMetadata::getId).toList();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<Map<String, Object>> readRecords(String fileName, List<String> fields) {
        try {
            WeatherDataset challengeResponse = objectMapper.readValue(new File(fileName), WeatherDataset.class);
            return challengeResponse.getRecords().stream().map(rawRecord -> IntStream.range(0, fields.size()).boxed().collect(Collectors.toMap(fields::get, i -> Optional.ofNullable(rawRecord.get(i)).orElse("0.0"), (first, second) -> first))).toList();
        } catch (Exception e) {
            System.err.println("ERORR: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static void getGeneralStatistics(List<String> fields, List<Map<String, Object>> records) {
        String fileName = "outputs/generalStatistics.txt";
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println("Statistics - General");
            buffer.println("============================================================");
            for (int i = 9; i < fields.size(); i++) {
                String field = fields.get(i);
                DoubleSummaryStatistics stats = records.stream()
                        .map(map -> map.get(field))
                        .filter(Objects::nonNull)
                        .mapToDouble(val -> Double.parseDouble(val.toString()))
                        .summaryStatistics();
                buffer.println("\n[ " + formatIdName(field) + " ]");
                buffer.printf(
                        "    \tAvg : %8.2f | Max : %8.2f | Min : %8.2f%n",
                        stats.getAverage(),
                        stats.getMax(),
                        stats.getMin()
                );
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    public static <type> void getStatisticsBy(List<String> fields, Map<type, List<Map<String, Object>>> filteredRecords, Function<type, String> formatter , String reportTitle , String fileName){
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println(reportTitle);
            buffer.println("============================================================");
            for(var entry : filteredRecords.entrySet()){
                type groupKey = entry.getKey();
                List<Map<String, Object>> groupRecords = entry.getValue();
                buffer.println("\n- " + formatter.apply(groupKey) + ":");
                for(int i = 9; i < fields.size(); i++){
                    String field = fields.get(i);
                    DoubleSummaryStatistics stats = groupRecords.stream()
                            .map(map -> map.get(field))
                            .filter(Objects::nonNull)
                            .mapToDouble(value -> Double.parseDouble(value.toString()))
                            .summaryStatistics();
                    buffer.println("\n\t[ " + formatIdName(field) + " ]");
                    buffer.printf(
                            "    \tAvg : %8.2f | Max : %8.2f | Min : %8.2f%n",
                            stats.getAverage(),
                            stats.getMax(),
                            stats.getMin()
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
