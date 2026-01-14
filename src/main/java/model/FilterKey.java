package model;

public record FilterKey(
    FilterType filterType,
    String value
){
    public static FilterKey buildKey(FilterType type, WeatherRecord weatherRecord){
        return switch (type){
            case DAY -> new FilterKey(FilterType.DAY, weatherRecord.date() + ' ' + weatherRecord.dayOfWeek());
            case HOUR -> new FilterKey(FilterType.HOUR, weatherRecord.hour());
            case LOCATION -> new FilterKey(FilterType.LOCATION, weatherRecord.location());
            case GLOBAL -> new FilterKey(FilterType.GLOBAL , "ALL");
        };
    }
}
