package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.WeatherFieldMetadata;
import model.WeatherDataset;
import model.WeatherRecord;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
    WeatherStatisticsRepository.
    In charge of data loading and fields parsing.
* */
public class WeatherStatisticsRepository {
    List<String> fields;
    List<WeatherRecord> weatherRecords;

    public List<WeatherRecord> getWeatherRecords() {
        return weatherRecords;
    }

    public WeatherStatisticsRepository(String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WeatherDataset weatherDataset = objectMapper.readValue(new File(fileName), WeatherDataset.class);
            fields = weatherDataset.getFields().stream()
                    .map(WeatherFieldMetadata::getId)
                    .toList();
            weatherRecords = weatherDataset.getRecords().stream()
                    .map(this::toWeatherRecord)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error loading weather data", e);
        }
    }

    private WeatherRecord toWeatherRecord(List<Object> raw) {
        int limit = Math.min(fields.size(), raw.size());
        int METRICS_START_INDEX = 9;
        Map<String, Object> metrics =
                IntStream.range(0, Math.min(METRICS_START_INDEX, limit))
                        .boxed()
                        .map(i -> new AbstractMap.SimpleEntry<>(fields.get(i), raw.get(i)))
                        .filter(e -> e.getKey() != null && e.getValue() != null)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a
                        ));

        Map<String, Double> countableMetrics =
                IntStream.range(METRICS_START_INDEX, limit)
                        .boxed()
                        .map(i -> new AbstractMap.SimpleEntry<>(fields.get(i), raw.get(i)))
                        .filter(e -> e.getKey() != null && e.getValue() != null)
                        .flatMap(e -> {
                            try {
                                return Stream.of(
                                        new AbstractMap.SimpleEntry<>(
                                                e.getKey(),
                                                Double.parseDouble(e.getValue().toString())
                                        )
                                );
                            } catch (NumberFormatException ex) {
                                return Stream.empty();
                            }
                        })
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
        String time = metrics.get("time").toString();
        return new WeatherRecord(
                time.substring(0, 10),
                time.substring(11, 16),
                metrics.get("name").toString(),
                countableMetrics,
                metrics.get("dayofweek").toString()
        );
    }

}
