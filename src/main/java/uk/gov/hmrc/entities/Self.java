package uk.gov.hmrc.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class Self {
    @JsonProperty("href") @Setter @Getter String href;

    @Override
    public String toString() {
        return "Self{" +
                "href='" + href + '\'' +
                '}';
    }
}
