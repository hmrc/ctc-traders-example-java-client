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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import uk.gov.hmrc.entities.GetDepartureResponse;
import uk.gov.hmrc.entities.GetSingleDepartureMessageResponse;
import uk.gov.hmrc.entities.Links;
import uk.gov.hmrc.entities.Self;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServiceConnectorTest {
    @Mock
    RestTemplate restTemplate;
    @Captor
    ArgumentCaptor<HttpEntity<?>> entity;

    @MockBean
    ServiceConnector sut;

    @MockBean
    ObjectMapper mapper;

    private final String ACCESS_TOKEN_VALUE = "abcdefghijklmno";

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JSR310Module());
        sut = new ServiceConnector(restTemplate, mapper);
        sut.PHASE5_ACCEPT_HEADER = "application/vnd.hmrc.2.0+json";
        sut.submitDepartureDeclarationUrl = "http://somewhere.over/the/rainbow";
        sut.getSingleDepartureMessageUrl = "http://somewhere.over/the/rainbow";
    }

    @Test
    public void shouldCreateNewDepartureMovement() throws Exception {
        Optional<String> accessToken = Optional.of(ACCESS_TOKEN_VALUE);
        SubmitDepartureDeclarationResponse responseObject = new SubmitDepartureDeclarationResponse("/departures/abc123");
        given(restTemplate.exchange(eq(sut.submitDepartureDeclarationUrl), eq(HttpMethod.POST), entity.capture(), eq(SubmitDepartureDeclarationResponse.class))).willReturn(ResponseEntity.of(Optional.of(responseObject)));

        SubmitDepartureDeclarationResponse result = sut.createDepartureMovement("<CC015C></CC015C>", accessToken);

        assertThat(result, equalTo(responseObject));
        HttpHeaders headers = entity.getValue().getHeaders();
        assertThat(headers.getContentType(), is(MediaType.APPLICATION_XML));
    }

    @Test
    public void shouldRetrieveMessage() throws Exception {
        Optional<String> accessToken = Optional.of(ACCESS_TOKEN_VALUE);
        GetSingleDepartureMessageResponse responseObject = new GetSingleDepartureMessageResponse("body", new Links(new Self("self")));
        given(restTemplate.exchange(eq(sut.getSingleDepartureMessageUrl), eq(HttpMethod.GET), ArgumentMatchers.isNull(), eq(GetSingleDepartureMessageResponse.class), ArgumentMatchers.anyMap())).willReturn(ResponseEntity.of(Optional.of(responseObject)));

        GetSingleDepartureMessageResponse result = sut.getSingleDepartureMessage("a", "b", accessToken);

        assertThat(result, equalTo(responseObject));
    }

    @Test
    public void shouldRetrieveDeparture() throws Exception {
        Optional<String> accessToken = Optional.of(ACCESS_TOKEN_VALUE);
        GetDepartureResponse responseObject = new GetDepartureResponse(
            new Links(new Self("self")),
            "id", 
            "created", 
            "updated", 
            "enrollmentEORINumber",
            "movementEORINumber", 
            "movementReferenceNumber");

        given(restTemplate.exchange(eq(sut.getDepartureUrl), eq(HttpMethod.GET), ArgumentMatchers.isNull(), eq(GetDepartureResponse.class), ArgumentMatchers.anyMap())).willReturn(ResponseEntity.of(Optional.of(responseObject)));

        GetDepartureResponse result = sut.getDeparture("a", accessToken);

        assertThat(result, equalTo(responseObject));
    }

}