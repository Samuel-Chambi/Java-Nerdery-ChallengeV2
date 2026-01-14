package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FieldMetadata;
import model.WeatherDataset;
import model.WeatherRecord;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeatherStatisticsRepository {
    List<String> fields;
    List<WeatherRecord> weatherRecords;
    public WeatherStatisticsRepository(String fileName){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            WeatherDataset weatherDataset = objectMapper.readValue(new File(fileName), WeatherDataset.class);
            fields = weatherDataset.getFields().stream()
                    .map(FieldMetadata::getId)
                    .toList();
            weatherRecords = weatherDataset.getRecords().stream()
                    .map(this::toWeatherRecord)
                    .toList();
        }catch(Exception e){
            throw new RuntimeException("Error loading weather data", e);
        }
    }
    private WeatherRecord toWeatherRecord(List<Object> raw){
        Map<String, Object> metrics = IntStream.range(0, fields.size())
                .boxed()
                .collect(Collectors.toMap(
                        fields::get,
                        raw::get
                ));
        String time = metrics.get("time").toString();
        return new WeatherRecord(
                time.substring(0 , 10),
                time.substring(11 , 16),
                metrics.get("name").toString(),
                extractMetrics(metrics),
                metrics.get("dayofweek").toString()
        );
    }

    private Map<String, Double> extractMetrics(Map<String , Object> metrics){
        return IntStream.range(9 , fields.size())
                .boxed()
                .map(i -> Map.entry(fields.get(i),Double.parseDouble(metrics.get(fields.get(i)).toString())))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
