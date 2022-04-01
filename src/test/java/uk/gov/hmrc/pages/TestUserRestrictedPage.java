package uk.gov.hmrc.pages;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmrc.WicketWebApplication;
import uk.gov.hmrc.pages.HomePage;
import uk.gov.hmrc.pages.SubmitDepartureDeclarationPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestUserRestrictedPage {
    private WicketTester tester;

    @Autowired
    private WicketWebApplication application;

    @BeforeEach
    public void setUp() {
        tester = new WicketTester(application);
    }

    @Test
    public void homepageRedirectToGG() {
        //start and render the test page
        tester.startPage(HomePage.class);

        //assert the page was redirected to GG auth
        assertEquals("https://test-api.service.hmrc.gov.uk//oauth/authorize?response_type=code&client_id=change-me&scope=common-transit-convention-traders&redirect_uri=https://localhost", tester.getLastResponse().getRedirectLocation());
    }

    @Test
    public void departureDeclarationRedirectToGG() {
        //start and render the test page
        tester.startPage(SubmitDepartureDeclarationPage.class);

        //assert the page was redirected to GG auth
        assertEquals("https://test-api.service.hmrc.gov.uk//oauth/authorize?response_type=code&client_id=change-me&scope=common-transit-convention-traders&redirect_uri=https://localhost", tester.getLastResponse().getRedirectLocation());
    }

}
