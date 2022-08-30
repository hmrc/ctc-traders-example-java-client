/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.datetime.StyleDateConverter;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;
import uk.gov.hmrc.components.MenuPanel;
import uk.gov.hmrc.entities.OauthPair;
import uk.gov.hmrc.services.AuthService;
import uk.gov.hmrc.services.InvalidAuthenticationCodeException;

import java.time.ZoneOffset;
import java.util.Date;

public abstract class UserRestrictedPage extends AuthAwarePage {

    @SpringBean
    AuthService authService;

    public UserRestrictedPage(PageParameters parameters) {
        super(parameters);

        super.onBeforeRender();
        // No valid access token?  That means that we've not gone through the
        // Government Gateway sign-in and grant screens yet.
        OauthPair oauthPair = authService.getCurrentOauthPair();
        if ((oauthPair == null || !oauthPair.isValid()) && !authService.attemptRefreshOauthPair()) {
            StringValue code = getPageParameters().get("code");
            if (code.isEmpty()) {
                redirectToGovernmentGateway();
            } else {
                try {
                    authService.createNewOauthPair(code.toOptionalString());
                } catch (InvalidAuthenticationCodeException e) {
                    redirectToGovernmentGateway();
                }
            }
        }

        add(new MenuPanel("menu"));

        Label currentAccessToken = authService.isOAuthPairValid() ?
                new DateLabel("accessValidUntil", new LoadableDetachableModel<>() {

                    @Override
                    protected Date load() {
                        return Date.from(authService.getCurrentOauthPair().getAccessValidUntil().toInstant(ZoneOffset.UTC));
                    }
                }, new StyleDateConverter("MM", true)) : new Label("accessValidUntil", "Not logged in");
        add(currentAccessToken);
    }

    /**
     * Send a redirect to the browser to show the Government Gateway login page
     */
    protected void redirectToGovernmentGateway() {
        throw new RedirectToUrlException(
                authService.getAuthUrl() + "/oauth/authorize?response_type=code" +
                        "&client_id=" + authService.getClientId() +
                        "&scope=" + authService.getScope() +
                        "&redirect_uri=" + authService.getRedirectUrl());
    }
}
