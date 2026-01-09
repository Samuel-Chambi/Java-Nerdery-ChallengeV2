import DTOs.ChallengeResponse;
import DTOs.FieldMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
   Mejoras
 * Parsea los valores de las caracteristicas del JSON
 * Aniade mas clasificaciones - (Filtro por hora, lugar, fecha)
 * Encontrar la forma de presentar la informacion de una forma mas agradable
 * Mejorar las soluciones (Codigo mas legible, funcional)
*/

public class Main {
    public static void main(String[] args) {
        String fileName = "src/main/resources/WeatherStations.json";
        List<String> fields = readFields(fileName);
        List<Map<String, Object>> records = readRecords(fileName, fields);
        // Reporte general
        getGeneralStatistics(fields, records);
        // Reporte filtrado por fecha
        List<String> dates = getUniqueDates(records);
        getDayStatistics(fields, records, dates);
        // Reporte filtrado por hora
        List<String> hours = getUniqueHours(records);
        getHourStatistics(fields, records, hours);
        // Reporte filtrado por lugar
        List<String> locations = getUniqueLocations(records);
        getLocationStatistics(fields, records , locations);
    }

    private static String formatIdName(String id) {
        if (id == null || id.isEmpty()) return "";
        String cleaned = id.replaceAll("^_+", "").replace("_", "");
        String spaced = cleaned.replaceAll("(?i)(temp|pressure|speed|direction|humidity|distance|record|of|week)", " $1").trim();
        return spaced.substring(0, 1).toUpperCase() + spaced.substring(1).toLowerCase();
    }

    public static List<String> readFields(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChallengeResponse challengeResponse = objectMapper.readValue(new File(fileName), ChallengeResponse.class);
            return challengeResponse.getFields().stream()
                    .map(FieldMetadata::getId)
                    .toList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return Collections.emptyList();
    }

    public static List<Map<String, Object>> readRecords(String fileName, List<String> fields) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChallengeResponse challengeResponse = objectMapper.readValue(new File(fileName), ChallengeResponse.class);
            return challengeResponse.getRecords().stream()
                    .map(rawRecord -> IntStream.range(0, fields.size())
                            .boxed()
                            .collect(Collectors.toMap(
                                    fields::get,
                                    i -> Optional.ofNullable(rawRecord.get(i)).orElse("0.0"),
                                    (first, second) -> first
                            )))
                    .toList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return Collections.emptyList();
    }

    public static void getGeneralStatistics(List<String> fields, List<Map<String, Object>> records) {
        String fileName = "outputs/generalStatistics.txt";
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println("GENERAL REPORT");
            buffer.println("============================================================");
            for (int i = 9; i < fields.size(); i++) {
                String field = fields.get(i);
                double average, minimum, maximum;
                List<Double> values = records.stream()
                        .map(map -> map.get(field))
                        .filter(Objects::nonNull)
                        .map(val -> Double.parseDouble(val.toString()))
                        .toList();
                average = values.stream().reduce(Double::sum).orElse(0.0);
                average = values.isEmpty() ? average : average / values.size();
                minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                maximum = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                buffer.println("\n[ " + formatIdName(field) + " ]");
                buffer.printf("    %-18s : %12.2f%n", "Avg. value", average);
                buffer.printf("    %-18s : %12.2f%n", "Max. value", maximum);
                buffer.printf("    %-18s : %12.2f%n", "Min. value", minimum);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    public static String getDayOfWeek(List<Map<String, Object>> records, String date) {
        return records.stream()
                .filter(map -> {
                    Object time = map.get("time");
                    return time != null && time.toString().startsWith(date);
                })
                .map(map -> map.get("dayofweek"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .findFirst().orElse("");
    }

    private static List<String> getUniqueDates(List<Map<String, Object>> records) {
        return records.stream()
                .map(map -> map.get("time"))
                .filter(Objects::nonNull)
                .map(value -> value.toString().substring(0, 10))
                .distinct()
                .toList();
    }

    private static List<String> getUniqueHours(List<Map<String , Object>> records) {
        return records.stream()
                .map(map -> map.get("time"))
                .filter(Objects::nonNull)
                .map(value -> value.toString().substring(11,16))
                .distinct()
                .toList();
    }

    private static List<String> getUniqueLocations(List<Map<String, Object>> records){
        return records.stream()
                .map(map -> map.get("name"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .toList();
    }

    public static void getDayStatistics(List<String> fields, List<Map<String, Object>> records, List<String> days) {
        String fileName = "outputs/perDayStatistics.txt";
        /*FIX: ONLY FOR 50 DIFFERENT DAYS*/
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println("REPORT GROUP BY DAY");
            buffer.println("============================================================");
            int cnt = 0;
            for (String day : days) {
                if(cnt++ == 50) break;
                buffer.println("\n[ " + day + " " + getDayOfWeek(records, day) + " ]");
                for (int i = 9; i < fields.size(); i++) {
                    String field = fields.get(i);
                    double average, minimum, maximum;
                    List<Double> values = records.stream()
                            .filter(map -> {
                                Object time = map.get("time");
                                return time != null && time.toString().startsWith(day);
                            })
                            .map(map -> map.get(field))
                            .filter(Objects::nonNull)
                            .map(val -> Double.parseDouble(val.toString()))
                            .toList();
                    average = values.stream().reduce(Double::sum).orElse(0.0);
                    average = values.isEmpty() ? average : average / values.size();
                    minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    maximum = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    buffer.println("\n\t[[ " + formatIdName(field) + " ]]");
                    buffer.printf("    \t%-18s : %12.2f%n", "Avg. value", average);
                    buffer.printf("    \t%-18s : %12.2f%n", "Max. value", maximum);
                    buffer.printf("    \t%-18s : %12.2f%n", "Min. value", minimum);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public static void getHourStatistics(List<String> fields, List<Map<String, Object>> records, List<String> hours) {
        String fileName = "outputs/perHourStatistics.txt";
        /*FIX: ONLY FOR 50 DIFFERENT HOURS or LESS*/
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println("REPORT GROUP BY HOUR");
            buffer.println("============================================================");
            int cnt = 0;
            for (String hour : hours) {
                if(cnt++ == 50) break;
                buffer.println("\n[ " + hour + " ]");
                for (int i = 9; i < fields.size(); i++) {
                    String field = fields.get(i);
                    double average, minimum, maximum;
                    List<Double> values = records.stream()
                            .filter(map -> {
                                Object time = map.get("time");
                                return time != null && time.toString().substring(11 , 16).equals(hour);
                            })
                            .map(map -> map.get(field))
                            .filter(Objects::nonNull)
                            .map(val -> Double.parseDouble(val.toString()))
                            .toList();
                    average = values.stream().reduce(Double::sum).orElse(0.0);
                    average = values.isEmpty() ? average : average / values.size();
                    minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    maximum = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    buffer.println("\n\t[[ " + formatIdName(field) + " ]]");
                    buffer.printf("    \t%-18s : %12.2f%n", "Avg. value", average);
                    buffer.printf("    \t%-18s : %12.2f%n", "Max. value", maximum);
                    buffer.printf("    \t%-18s : %12.2f%n", "Min. value", minimum);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public static void getLocationStatistics(List<String> fields, List<Map<String, Object>> records, List<String> locations) {
        String fileName = "outputs/perLocationStatistics.txt";
        /*FIX: ONLY FOR 50 DIFFERENT LOCATIONS or LESS*/
        try (PrintWriter buffer = new PrintWriter(fileName)) {
            buffer.println("============================================================");
            buffer.println("REPORT GROUP BY LOCATION");
            buffer.println("============================================================");
            int cnt = 0;
            for (String location : locations) {
                if(cnt++ == 50) break;
                buffer.println("\n[ " + location + " ]");
                for (int i = 9; i < fields.size(); i++) {
                    String field = fields.get(i);
                    double average, minimum, maximum;
                    List<Double> values = records.stream()
                            .filter(map -> {
                                Object time = map.get("name");
                                return time != null && time.toString().equals(location);
                            })
                            .map(map -> map.get(field))
                            .filter(Objects::nonNull)
                            .map(val -> Double.parseDouble(val.toString()))
                            .toList();
                    average = values.stream().reduce(Double::sum).orElse(0.0);
                    average = values.isEmpty() ? average : average / values.size();
                    minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    maximum = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    buffer.println("\n\t[[ " + formatIdName(field) + " ]]");
                    buffer.printf("    \t%-18s : %12.2f%n", "Avg. value", average);
                    buffer.printf("    \t%-18s : %12.2f%n", "Max. value", maximum);
                    buffer.printf("    \t%-18s : %12.2f%n", "Min. value", minimum);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

}
