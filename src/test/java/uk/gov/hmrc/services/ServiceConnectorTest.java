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
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
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
    ArgumentCaptor<HttpEntity<String>> entity;

    @MockBean
    ServiceConnector sut;

    @MockBean
    ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JSR310Module());
        sut = new ServiceConnector(restTemplate, mapper);
        sut.PHASE5_ACCEPT_HEADER = "application/vnd.hmrc.2.0+json";
        sut.submitDepartureDeclarationUrl = "http://somewhere.over/the/rainbow";
    }

    @Test
    public void shouldCreateNewDepartureMovement() throws Exception {
        Optional<String> accessToken = Optional.of("abcdefghijklmno");
        SubmitDepartureDeclarationResponse responseObject = new SubmitDepartureDeclarationResponse("abc123", "/departures/abc123");
        given(restTemplate.postForObject(eq(sut.submitDepartureDeclarationUrl), entity.capture(), eq(SubmitDepartureDeclarationResponse.class))).willReturn(responseObject);

        SubmitDepartureDeclarationResponse result = sut.createDepartureMovement("<CC015C></CC015C>", accessToken);

        assertThat(result, equalTo(responseObject));
        HttpHeaders headers = entity.getValue().getHeaders();
        assertThat(headers.get("Authorization"), hasItem("Bearer " + accessToken.get()));
        assertThat(headers.getAccept(), hasItem(MediaType.valueOf(sut.PHASE5_ACCEPT_HEADER)));
        assertThat(headers.getContentType(), is(MediaType.APPLICATION_XML));
    }

}