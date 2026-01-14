package model;

import java.util.Map;

public record WeatherRecord(
        String date,
        String hour,
        String location,
        Map<String, Double> metrics,
        String dayOfWeek
){}
