package uk.gov.hmrc.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.gov.hmrc.entities.GetSingleDepartureMessageRequest;
import uk.gov.hmrc.entities.GetSingleDepartureMessageResponse;
import uk.gov.hmrc.services.CTCTradersService;
import uk.gov.hmrc.services.NotFoundException;
import uk.gov.hmrc.services.RequestException;
import uk.gov.hmrc.services.UnauthorizedException;

@CommonsLog
public class GetSingleDepartureMessageForm extends Form<GetSingleDepartureMessageRequest> {

    @SpringBean
    CTCTradersService service;

    @SpringBean
    ObjectMapper mapper;

    public GetSingleDepartureMessageForm(String id) {
        super(id, new CompoundPropertyModel<>(new GetSingleDepartureMessageRequest()));
        add(new RequiredTextField<String>("departureId"));
        add(new RequiredTextField<String>("messageId"));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        final GetSingleDepartureMessageRequest request = (GetSingleDepartureMessageRequest) getDefaultModelObject();
        log.debug("Declaration Data form submitted.");
        log.debug("* Departure ID: " + request.getDepartureId());
        log.debug("* Message ID: " + request.getMessageId());
        try {
            GetSingleDepartureMessageResponse response = service.getMessageForDeparture(request);
            info("Returned message successfully.");
            log.debug(response);
            info("Body: " + response.getBody());
            info("Message reference: " + response.getLinks().getSelf().getHref());
        } catch (UnauthorizedException e) {
            log.error("Unable to get a message: server returned unauthorised");
            error("Unable to get a message: server returned unauthorised");
        } catch (NotFoundException e) {
            log.error("Unable to get a message: not found or archived");
            error("Unable to get a message: not found or archived");
        } catch (RequestException e){
            log.error("Unable to submit the declaration: "+e.getMessage(), e);
            error("Unable to get a message: an unexpected error occurred");
        }
    }
}
