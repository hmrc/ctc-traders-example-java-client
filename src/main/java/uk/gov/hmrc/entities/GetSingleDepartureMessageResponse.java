package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSingleDepartureMessageResponse {

    @JsonProperty("body")
    private String body;

    @JsonProperty("_links")
    private Links links;

}
