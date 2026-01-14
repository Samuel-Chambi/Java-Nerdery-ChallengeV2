package service;

import model.FilterKey;
import model.FilterType;
import model.WeatherRecord;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static model.FilterKey.buildKey;


public class StatisticsService {
    private final List<WeatherRecord> records;
    public StatisticsService(List<WeatherRecord> records){
        this.records = records;
    }
    public Map<FilterKey, Map<String, DoubleSummaryStatistics>> getStatisticsBy(FilterType filterType){
        return records.stream()
                .collect(Collectors.groupingBy(
                        r -> buildKey(filterType , r),
                        Collectors.flatMapping(
                                r -> r.metrics().entrySet().stream(),
                                Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.summarizingDouble(Map.Entry::getValue)
                                )
                        )
                ));
    }
}
