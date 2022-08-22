package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Links {
    @JsonProperty("self") @Setter @Getter Self self;

    @Override
    public String toString() {
        return "Links{" +
                "self=" + self +
                '}';
    }
}
