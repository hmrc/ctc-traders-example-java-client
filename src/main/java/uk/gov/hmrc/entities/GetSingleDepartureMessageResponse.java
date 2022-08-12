package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetSingleDepartureMessageResponse {

    @JsonProperty("body")
    private String body;

    @JsonProperty("_links")
    private Links links;

}
