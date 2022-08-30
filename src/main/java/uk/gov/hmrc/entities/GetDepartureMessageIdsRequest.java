package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Data
public class GetDepartureMessageIdsRequest implements Serializable {

    @JsonProperty("departureId")
    private String departureId;

    @JsonProperty("receivedSince")
    private LocalDateTime receivedSince = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

    public Optional<ZonedDateTime> getDate() {
        return Optional.ofNullable(this.receivedSince).filter(x -> x.toInstant(ZoneOffset.UTC).isAfter(Instant.EPOCH)).map(x -> x.atZone(ZoneOffset.UTC));
    }

}
