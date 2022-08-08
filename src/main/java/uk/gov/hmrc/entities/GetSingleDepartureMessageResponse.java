package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetSingleDepartureMessageResponse {

    @JsonProperty("body")
    private final String body;

    @JsonProperty("_links")
    private final Links links;

}
