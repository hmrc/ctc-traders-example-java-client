package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDepartureResponse {
    @JsonProperty("_links")
    private Links links;

    @JsonProperty("id")
    private String id;

    @JsonProperty("created")
    private String created;

    @JsonProperty("updated")
    private String updated;

    @JsonProperty("enrollmentEORINumber")
    private String enrollmentEORINumber;

    @JsonProperty("movementEORINumber")
    private String movementEORINumber;

    @JsonProperty("movementReferenceNumber")
    private String movementReferenceNumber;
}
