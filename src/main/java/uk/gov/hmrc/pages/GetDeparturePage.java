package uk.gov.hmrc.pages;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import uk.gov.hmrc.components.GetDepartureForm;

public class GetDeparturePage extends UserRestrictedPage {
    
    private static final long serialVersionUID = 1L;
    
    public GetDeparturePage(PageParameters parameters) {
        super(parameters);
        add(new FeedbackPanel("feedback"));
        add(new GetDepartureForm("form"));
    }
}
