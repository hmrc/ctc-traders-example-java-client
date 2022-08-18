package uk.gov.hmrc.entities;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetDepartureRequest implements Serializable {
    @JsonProperty("id")
    private String departureId;
}
