package uk.gov.hmrc.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.gov.hmrc.components.errors.ValidationError;
import uk.gov.hmrc.components.errors.ValidationErrors;
import uk.gov.hmrc.entities.DepartureDeclaration;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;
import uk.gov.hmrc.services.CTCTradersService;
import uk.gov.hmrc.services.MessageSubmissionException;

import java.net.HttpURLConnection;

@CommonsLog
public class DepartureDeclarationForm extends Form<DepartureDeclaration> {

    @SpringBean
    CTCTradersService service;

    @SpringBean
    ObjectMapper mapper;

    public DepartureDeclarationForm(String id) {
        super(id, new CompoundPropertyModel<>(new DepartureDeclaration()));
        add(new RequiredTextField<String>("identificationNumber"));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        DepartureDeclaration declaration = (DepartureDeclaration)getDefaultModelObject();
        log.debug("Declaration Data form submitted.  EORI: "+declaration.getIdentificationNumber());
        try {
            SubmitDepartureDeclarationResponse response = service.createDepartureMovement(declaration);
            info("Submitted declaration successfully");
            log.debug(response);
            info("Movement id: " + response.getId());
            info("Movement reference: " + response.getLinks().getSelf().getHref());
        } catch(MessageSubmissionException e){
            log.error("Unable to submit the declaration: "+e.getMessage(), e);
            ValidationErrors errors = formatError(e);
            for ( ValidationError error : errors.getValidationErrors()) {
                error(error.toString());
            }
        }
    }

    protected ValidationErrors formatError(MessageSubmissionException e){
        try {
            switch (e.getStatusCode()) {
                case  HttpURLConnection.HTTP_BAD_REQUEST:
                    return mapper.readValue(e.getMessage(), ValidationErrors.class);
                default:
                    throw new RuntimeException( "Unknown status code "+e.getStatusCode()+" "+e.getMessage());
            }
        } catch( JsonProcessingException je){
            log.error(je);
            throw new RuntimeException((je));
        }
    }

}
