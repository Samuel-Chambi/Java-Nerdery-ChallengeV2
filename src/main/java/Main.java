import DTOs.ChallengeResponse;
import DTOs.FieldMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fileName = "src/main/resources/WeatherStations.json";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            ChallengeResponse challengeResponse = objectMapper.readValue(new File(fileName) , ChallengeResponse.class);

            List<String> fields = challengeResponse.getFields().stream()
                    .map(FieldMetadata::getId)
                    .toList();

            List<Map<String , Object>> records = new ArrayList<>();

            for(List<Object> rawRecord : challengeResponse.getRecords()){
                Map<String , Object> objectMap = new HashMap<>();
                for(int i = 0; i < fields.size(); i++){
                    objectMap.put(fields.get(i) , rawRecord.get(i));
                }
                records.add(objectMap);
            }

            /*Funcion */
            getGeneralStatistics(fields , records );
            /*FIX: day format until now: yyyy-mm-dd*/
//            List<String> dates = records.stream()
//                    .flatMap(map -> map.entrySet().stream())
//                    .filter(entryKey -> entryKey.getKey().equals("time"))
//                    .map(entryKey -> entryKey.getValue().toString().substring(0 , 10))
//                    .distinct().toList();
//            List<String> days = dates.stream()
//                    .map(date -> getDayOfWeek(records , date))
//                    .toList();
////            for(String aa : days) System.out.println(aa);
//            System.out.println("Total dates: " + dates.size());
//            System.out.println("Total days: " + days.size());
//            int LIMIT = 10;
//            for(int i =0 ; i < LIMIT; i++) {
//                String date = dates.get(i);
//                String day  = days.get(i);
//                System.out.println(date + ' ' + day + ":");
//                getDayStatistics(fields, records, date);
//            }
        }catch (Exception e){
//            System.err.println("Error: " + e);
            e.printStackTrace();
        }

    }
    /*Devuelve las estadisticas promedio , maximas, minimas , etc. segun el dia que se solicite, en caso no brinden un dia en especifico, devuelven todas las estadisticas en general */
    public static void getGeneralStatistics(List<String> fields , List<Map<String , Object>> records){
            /*Los valores a evaluar como tal, comienzan en el indice 9*/
            for(int i = 9; i < fields.size(); i++){
                String field = fields.get(i);
                double average , minimum , maximun;
                List<Double> values = records.stream()
                        .map(map -> map.get(field))
                        .filter(Objects::nonNull)
                        .map(val -> Double.parseDouble(val.toString()))
                        .toList();
                average = values.stream().reduce(Double::sum).orElse(0.0);
                average = values.isEmpty() ? average : average / values.size();
                minimum = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                maximun = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                System.out.println("- " + field + ": ");
                System.out.println("\tAverage value: " + average);
                System.out.println("\tMaximum value: " + maximun);
                System.out.println("\tMinimum value: " + minimum);
            }
    }
    public static String getDayOfWeek(List<Map<String, Object>> records, String date){
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
    public static void getDayStatistics(List<String> fields , List<Map<String , Object>> records, String day){
        for(int i = 9; i < fields.size(); i++){
            String field = fields.get(i);
            double average , minimum , maximum;
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
            System.out.println("\t- " + field + ": ");
            System.out.println("\t\tAverage value: " + average);
            System.out.println("\t\tMaximum value: " + maximum);
            System.out.println("\t\tMinimum value: " + minimum);
        }
    }
}
