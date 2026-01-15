package app;

import model.FilterType;
import model.WeatherRecord;
import report.ReportWriter;
import repository.WeatherStatisticsRepository;
import service.StatisticsService;

import java.util.List;

public class WeatherApp {
    /*
    * Main function: Only in charge from run app logic
    * */
    public static void main(String[] args) {
        //Measuring time for JSON reading
        long start = System.nanoTime();
        WeatherStatisticsRepository repository = new WeatherStatisticsRepository("src/main/resources/WeatherStations.json");
        long end = System.nanoTime();
        System.out.printf(
                "%-25s : %8.2f ms%n",
                "Reading time",
                (end - start) / 1_000_000.0
        );
        List<WeatherRecord> weatherRecords = repository.getWeatherRecords();
        StatisticsService statisticsService = new StatisticsService(weatherRecords);
        // Measuring time for Statistics calculation and generation
        timed("General Statistics", () -> ReportWriter.print(statisticsService.getStatisticsBy(FilterType.GLOBAL), "outputs/generalStats.txt", "STATISTICS - General"));
        timed("Day statistics", () -> ReportWriter.print(statisticsService.getStatisticsBy(FilterType.DAY), "outputs/dayStats.txt", "STATISTICS - Filtered by Day"));
        timed("Hour statistics", () -> ReportWriter.print(statisticsService.getStatisticsBy(FilterType.HOUR), "outputs/hourStats.txt", "STATISTICS - Filtered by Hour"));
        timed("Location statistics", () -> ReportWriter.print(statisticsService.getStatisticsBy(FilterType.LOCATION), "outputs/locationStats.txt", "STATISTICS - Filtered by Location"));
    }

    private static void timed(String title, Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        System.out.printf(
                "%-25s : %8.2f ms%n",
                title,
                (endTime - startTime) / 1_000_000.0
        );
    }
}
