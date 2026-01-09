package DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeResponse {
    private List<FieldMetadata> fields;
    private List<List<Object>> records;

    public List<FieldMetadata> getFields() {
        return fields;
    }

    public void setFields(List<FieldMetadata> fields) {
        this.fields = fields;
    }

    public List<List<Object>> getRecords() {
        return records;
    }

    public void setRecords(List<List<Object>> records) {
        this.records = records;
    }
}

