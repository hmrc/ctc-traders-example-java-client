package uk.gov.hmrc.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends UserRestrictedPage {
    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
        super(parameters);
    }
}
