package uk.gov.hmrc.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.gov.hmrc.entities.GetDepartureMessageIdsRequest;
import uk.gov.hmrc.entities.MessageId;
import uk.gov.hmrc.services.CTCTradersService;
import uk.gov.hmrc.services.NotFoundException;
import uk.gov.hmrc.services.RequestException;
import uk.gov.hmrc.services.UnauthorizedException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@CommonsLog
public class GetDepartureMessageIdsForm extends Form<GetDepartureMessageIdsRequest> {

    @SpringBean
    CTCTradersService service;

    @SpringBean
    ObjectMapper mapper;

    public GetDepartureMessageIdsForm(String id) {
        super(id, new CompoundPropertyModel<>(new GetDepartureMessageIdsRequest()));
        add(new RequiredTextField<String>("departureId"));
        add(new LocalDateTimeTextField("receivedSince", "dd-MM-yyyy HH:mm:ss"));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        final GetDepartureMessageIdsRequest request = (GetDepartureMessageIdsRequest) getDefaultModelObject();
        log.debug("Declaration Data form submitted.");
        log.debug("* Departure ID: " + request.getDepartureId());
        log.debug("* Received Since Filter: " + request
                .getDate()
                .map(ZonedDateTime::toOffsetDateTime)
                .map(DateTimeFormatter.ISO_DATE_TIME::format)
                .orElse("not specified")
        );
        try {
            var response = service.getMessageIdsForDeparture(request);
            info("Returned message successfully.");
            log.debug(response);
            info("Message IDs:");
            response.getMessages().forEach(x -> info("  " + x.getId()));
        } catch (UnauthorizedException e) {
            log.error("Unable to get message IDs: server returned unauthorised");
            error("Unable to get message IDs: server returned unauthorised");
        } catch (NotFoundException e) {
            log.error("Unable to get message IDs: none were found or they were archived");
            error("Unable to get a message: none were found or they were archived");
        } catch (RequestException e){
            log.error("Unable to get message IDs: "+e.getMessage(), e);
            error("Unable to get message IDs: an unexpected error occurred");
        }
    }
}
