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

    public SubmitDepartureDeclarationResponse(String id, String location){
        setId(id);
        setLinks(new Links(new Self(location)));
    }

    @JsonProperty("id")
    @Setter @Getter String id = "not implemented yet";

    @JsonProperty("_links")
    @Setter @Getter Links links;

    @Override
    public String toString() {
        return "SubmitDepartureDeclarationResponse{" +
                "id='" + id + '\'' +
                ", links=" + links +
                '}';
    }
}

