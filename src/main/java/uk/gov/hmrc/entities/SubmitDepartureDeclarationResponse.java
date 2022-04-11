package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@JsonIgnoreProperties
@NoArgsConstructor
public class SubmitDepartureDeclarationResponse implements Serializable {

    public SubmitDepartureDeclarationResponse(String id, String messageType, String location){
        setId(id);
        setMessageType(messageType);
        setLinks(new Links(new Self(location)));
    }

    @JsonProperty("departureId")
    @Setter @Getter String id;

    @JsonProperty("messageType")
    @Setter @Getter String messageType;

    @JsonProperty("_links")
    @Setter @Getter Links links;
}

