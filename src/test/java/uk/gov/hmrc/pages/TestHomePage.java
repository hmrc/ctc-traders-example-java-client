package uk.gov.hmrc.pages;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmrc.WicketWebApplication;
import uk.gov.hmrc.entities.OauthPair;
import uk.gov.hmrc.pages.HomePage;
import uk.gov.hmrc.pages.SubmitDepartureDeclarationPage;
import uk.gov.hmrc.services.AuthService;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestHomePage {
    private WicketTester tester;

    @Autowired
    private WicketWebApplication application;

    @MockBean
    AuthService authService;

    @BeforeEach
    public void setUp() {
        tester = new WicketTester(application);
        when(authService.isOAuthPairValid()).thenReturn(true);
        when(authService.getCurrentOauthPair()).thenReturn(new OauthPair("none", "none", LocalDateTime.now()));
    }

    @Test
    public void homePageRender() {
        //start and render the test page
        tester.startPage(HomePage.class);

        //assert the page was redirected to GG auth
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void submitDepartureDeclarationPageRender() {
        //start and render the test page
        tester.startPage(SubmitDepartureDeclarationPage.class);

        //assert the page was redirected to GG auth
        tester.assertRenderedPage(SubmitDepartureDeclarationPage.class);
    }
}
