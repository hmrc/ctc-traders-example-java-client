package uk.gov.hmrc.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class DepartureDeclaration implements Serializable {
    @Getter @Setter private String eori;
}
