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
import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@CommonsLog
public class ServiceConnector {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ServiceConnector.class);

    @Value("${tax.acceptHeader}")
    String PHASE5_ACCEPT_HEADER;

    @Value("${tax.submitDepartureDeclarationUrl}")
    protected String submitDepartureDeclarationUrl;

    public ServiceConnector(@Autowired @Qualifier("plainRestTemplate") RestTemplate restTemplate,
                            @Autowired ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;
    }

    public String get(String url, Optional<String> accessToken) throws UnauthorizedException {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor(accessToken, PHASE5_ACCEPT_HEADER));
        restTemplate.setInterceptors(interceptors);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedException();
        }
        return response.getBody();
    }

    public SubmitDepartureDeclarationResponse createDepartureMovement(String xml, Optional<String> accessToken) throws MessageSubmissionException {
            logger.trace("Sending payload >>" + xml + "<<");

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.valueOf(PHASE5_ACCEPT_HEADER)));
            headers.setContentType(MediaType.APPLICATION_XML);
            accessToken.ifPresent(token -> headers.set("Authorization", "Bearer " + token));

            logger.debug(String.valueOf(headers));

            HttpEntity<String> entity = new HttpEntity<>(xml, headers);
            try {
                return restTemplate.postForObject(submitDepartureDeclarationUrl, entity, SubmitDepartureDeclarationResponse.class);
            }
            catch (HttpClientErrorException | HttpServerErrorException e) {
                log.warn(e);
                throw new MessageSubmissionException(e.getStatusCode().getReasonPhrase(), e.getMessage());
            }
    }

    static class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

        private final Optional<String> accessToken;
        private final String acceptHeader;

        public HeaderRequestInterceptor(Optional<String> accessToken, String acceptHeader) {
            this.accessToken = accessToken;
            this.acceptHeader = acceptHeader;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpRequest wrapper = new HttpRequestWrapper(request);
            accessToken.ifPresent(token -> wrapper.getHeaders().set("Authorization", "Bearer " + token));
            wrapper.getHeaders().setAccept(Arrays.asList(MediaType.valueOf(acceptHeader)));
            return execution.execute(wrapper, body);
        }
    }
}