package uk.gov.hmrc.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;
import uk.gov.hmrc.entities.GetDepartureRequest;
import uk.gov.hmrc.entities.GetDepartureResponse;
import uk.gov.hmrc.services.CTCTradersService;
import uk.gov.hmrc.services.NotFoundException;
import uk.gov.hmrc.services.RequestException;
import uk.gov.hmrc.services.UnauthorizedException;

@CommonsLog
public class GetDepartureForm extends Form<GetDepartureRequest> {
    @SpringBean
    CTCTradersService service;

    @SpringBean
    ObjectMapper mapper;

    public GetDepartureForm(String id) {
        super(id, new CompoundPropertyModel<>(new GetDepartureRequest()));
        add(new RequiredTextField<String>("departureId"));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        final GetDepartureRequest request = (GetDepartureRequest) getDefaultModelObject();
        log.debug("Declaration Data form submitted.");
        log.debug("* Departure ID: " + request.getDepartureId());

        try {
            GetDepartureResponse response = service.getDeparture(request);
            info("Returned departure successfully.");
            log.debug(response);
            info("id: " + response.getId());
            info("Message reference: " + response.getLinks().getSelf().getHref());
        } catch (UnauthorizedException e) {
            log.error("Unable to get departure: server returned unauthorised");
            error("Unable to get departure: server returned unauthorised");
        } catch (NotFoundException e) {
            log.error("Unable to get departure: not found or archived");
            error("Unable to get departure: not found or archived");
        } catch (RequestException e){
            log.error("Unable to submit the declaration: "+e.getMessage(), e);
            error("Unable to get departure: an unexpected error occurred");
        } catch (Throwable t) {
            log.error("Something unexpected happened", t);
        }
    }
}
