package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Data
public class GetDepartureMessageIdsRequest implements Serializable {

    @JsonProperty("departureId")
    private String departureId;

    @JsonProperty("receivedSinceFilter")
    private boolean filterOnDate = false;

    @JsonProperty("receivedSince")
    private ZonedDateTime receivedSince = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

    public Optional<ZonedDateTime> getDate() {
        if (filterOnDate) {
            return Optional.ofNullable(this.receivedSince);
        }
        return Optional.empty();
    }

}
