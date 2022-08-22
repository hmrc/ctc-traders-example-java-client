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
import uk.gov.hmrc.entities.*;

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

    public SubmitDepartureDeclarationResponse createDepartureMovement(DepartureDeclaration declaration) throws RequestException, UnauthorizedException {
        String payload = cc015Template.replace("{eori}", declaration.getIdentificationNumber()).replace("{now}", now());
        return serviceConnector.createDepartureMovement(payload, Optional.of(authService.getCurrentOauthPair().getAccessToken()));
    }

    public GetDepartureMessageIdsResponse getMessageIdsForDeparture(GetDepartureMessageIdsRequest request) throws RequestException, NotFoundException, UnauthorizedException {
        return serviceConnector.getDepartureMessageIds(request.getDepartureId(), request.getDate(), Optional.of(authService.getCurrentOauthPair().getAccessToken()));
    }

    public GetSingleDepartureMessageResponse getMessageForDeparture(GetSingleDepartureMessageRequest request) throws RequestException, NotFoundException, UnauthorizedException {
        return serviceConnector.getSingleDepartureMessage(request.getDepartureId(), request.getMessageId(), Optional.of(authService.getCurrentOauthPair().getAccessToken()));
    }

    public GetDepartureResponse getDeparture(GetDepartureRequest request) throws RequestException, NotFoundException, UnauthorizedException {
        return serviceConnector.getDeparture(request.getDepartureId(), Optional.of(authService.getCurrentOauthPair().getAccessToken()));
    }

    public String now(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

}
