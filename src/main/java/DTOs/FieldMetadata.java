package DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 'Jackson' will ignore fields that are not in the DTO
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldMetadata {
    private String id;
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
