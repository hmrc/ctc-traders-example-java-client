package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetSingleDepartureMessageRequest implements Serializable {

    @JsonProperty("id")
    private String departureId;

    @JsonProperty("messageId")
    private String messageId;

}
