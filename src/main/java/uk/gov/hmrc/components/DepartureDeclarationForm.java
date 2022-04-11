package uk.gov.hmrc.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.gov.hmrc.entities.DepartureDeclaration;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;
import uk.gov.hmrc.services.CTCTradersService;

@CommonsLog
public class DepartureDeclarationForm extends Form<DepartureDeclaration> {

    @SpringBean
    CTCTradersService service;

    @SpringBean
    ObjectMapper mapper;

    public DepartureDeclarationForm(String id) {
        super(id, new CompoundPropertyModel(new DepartureDeclaration()));
        add(new RequiredTextField("eori"));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        DepartureDeclaration declaration = (DepartureDeclaration)getDefaultModelObject();
        log.debug("Declaration Data form submitted.  EORI: "+declaration.getEori());
        try {
            SubmitDepartureDeclarationResponse response = service.createDepartureMovement(declaration.getEori());
            info("Submitted declaration successfully");
            info("Movement id: "+response.getId());
            info("Movement reference: "+response.getLinks().getSelf().getHref());
        } catch(Exception e){
            log.error("Unable to submit the declaration: "+e.getMessage(), e);
            error(e.getMessage());
        }
    }
}
