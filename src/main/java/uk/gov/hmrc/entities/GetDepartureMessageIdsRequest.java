package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Data
public class GetDepartureMessageIdsRequest implements Serializable {

    @JsonProperty("id")
    private String departureId;

    @JsonProperty("receivedSince-enabled")
    private boolean filterOnDate;

    @Nullable
    @JsonProperty("receivedSince")
    private Date receivedSince;

    public Optional<Date> getDate() {
        if (filterOnDate) {
            return Optional.ofNullable(this.receivedSince);
        }
        return Optional.empty();
    }

}
