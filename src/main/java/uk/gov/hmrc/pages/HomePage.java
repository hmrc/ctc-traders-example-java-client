package uk.gov.hmrc.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.StyleDateConverter;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;
import uk.gov.hmrc.components.MenuPanel;
import uk.gov.hmrc.services.AuthService;

import java.time.ZoneOffset;
import java.util.Date;


public class HomePage extends UserRestrictedPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    AuthService authService;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        add(new MenuPanel("menu"));

        Label currentAccessToken = authService.isOAuthPairValid() ?
                new DateLabel("accessValidUntil", new LoadableDetachableModel() {

                    @Override
                    protected Date load() {
                        return Date.from(authService.getCurrentOauthPair().getAccessValidUntil().toInstant(ZoneOffset.UTC));
                    }
                }, new StyleDateConverter("MM", true)) : new Label("accessValidUntil", "Not logged in");

        add(currentAccessToken);
    }
}
