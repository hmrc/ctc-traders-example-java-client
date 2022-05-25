package uk.gov.hmrc.components.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrors {
    @Setter @Getter String message;
    @Setter @Getter String code;
    @Setter @Getter List<ValidationError> validationErrors;

}
