package uk.gov.hmrc.components;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import uk.gov.hmrc.pages.*;


public class MenuPanel extends Panel {
    public MenuPanel(String id) {
        super(id);
        add(new BookmarkablePageLink("home", HomePage.class));
        add(new BookmarkablePageLink("departuredeclaration", SubmitDepartureDeclarationPage.class));
        add(new BookmarkablePageLink("getdeparturemessageids", GetMessageIdsForDeparturePage.class));
        add(new BookmarkablePageLink("getsingledeparturemessage", GetSingleDepartureMessagePage.class));
        add(new BookmarkablePageLink("getdeparture", GetDeparturePage.class));

    }
}
