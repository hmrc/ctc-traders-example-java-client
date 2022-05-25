package uk.gov.hmrc.components.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    @Setter @Getter int lineNumber;
    @Setter @Getter int columnNumber;
    @Setter @Getter String message;

    public String toString(){
        return "Line "+ lineNumber +" Column: "+ columnNumber +" Error: "+message;
    }
}
