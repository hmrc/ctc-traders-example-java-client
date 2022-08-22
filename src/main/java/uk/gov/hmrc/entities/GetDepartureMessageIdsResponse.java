package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDepartureMessageIdsResponse {

    @JsonProperty("messages")
    private List<MessageId> messages;

}
