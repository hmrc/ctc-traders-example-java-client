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
package uk.gov.hmrc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class CTCTradersService {

    @Autowired
    ServiceConnector serviceConnector;
    @Autowired
    AuthService authService;
    @Autowired @Qualifier("cc015")
    private String cc015Template;

    public SubmitDepartureDeclarationResponse createDepartureMovement(String eori) throws MessageSubmissionException {
        String payload = cc015Template.replaceAll("\\{eori}", eori).replaceAll("\\{now}", now());
        return serviceConnector.createDepartureMovement(payload, Optional.of(authService.getCurrentOauthPair().getAccessToken()));
    }

    public String now(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date());
    }
}
