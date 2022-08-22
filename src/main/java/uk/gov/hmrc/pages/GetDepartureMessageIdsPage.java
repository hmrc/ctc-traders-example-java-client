package uk.gov.hmrc.pages;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uk.gov.hmrc.components.GetDepartureMessageIdsForm;
import uk.gov.hmrc.components.GetSingleDepartureMessageForm;

public class GetDepartureMessageIdsPage extends UserRestrictedPage {

    private static final long serialVersionUID = 1L;

    public GetDepartureMessageIdsPage(PageParameters parameters) {
        super(parameters);
        add(new FeedbackPanel("feedback"));
        add(new GetDepartureMessageIdsForm("form"));
    }
}
