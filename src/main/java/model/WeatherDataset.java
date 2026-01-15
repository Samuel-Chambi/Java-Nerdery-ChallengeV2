package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// 'Jackson' will ignore fields that are not in the DTO
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDataset {
    private List<WeatherFieldMetadata> fields;
    private List<List<Object>> records;

    public List<WeatherFieldMetadata> getFields() {
        return fields;
    }

    public void setFields(List<WeatherFieldMetadata> fields) {
        this.fields = fields;
    }

    public List<List<Object>> getRecords() {
        return records;
    }

    public void setRecords(List<List<Object>> records) {
        this.records = records;
    }
}

