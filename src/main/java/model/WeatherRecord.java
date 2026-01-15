package model;

import java.util.Map;
/*
  Model for records
* Only stores needed information for app logic
* */
public record WeatherRecord(
        String date,
        String hour,
        String location,
        Map<String, Double> metrics,
        String dayOfWeek
) {
}
