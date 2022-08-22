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
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmrc.entities.GetDepartureMessageIdsResponse;
import uk.gov.hmrc.entities.GetDepartureResponse;
import uk.gov.hmrc.entities.GetSingleDepartureMessageResponse;
import uk.gov.hmrc.entities.SubmitDepartureDeclarationResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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


    @Value("${tax.getDepartureMessageIDsUrl}")
    protected String getDepartureMessageIDsUrl;

    @Value("${tax.getSingleDepartureMessageUrl}")
    protected String getSingleDepartureMessageUrl;

    @Value("${tax.getDepartureUrl}")
    protected String getDepartureUrl;

    public ServiceConnector(@Autowired @Qualifier("plainRestTemplate") RestTemplate restTemplate,
                            @Autowired ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;
    }

    protected <T> ResponseEntity<T> request(
            HttpMethod method,
            String url,
            @Nullable Map<String, String> uriParameters,
            @Nullable HttpEntity<?> requestBody,
            Class<T> responseType,
            Optional<String> accessToken
    ) throws UnauthorizedException {
        try {
            restTemplate.setInterceptors(List.of(new HeaderRequestInterceptor(accessToken, PHASE5_ACCEPT_HEADER)));
            final ResponseEntity<T> response;
            if (uriParameters == null) {
                response = restTemplate.exchange(url, method, requestBody, responseType);
            } else {
                response = restTemplate.exchange(url, method, requestBody, responseType, uriParameters);
            }

            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException();
            }
            throw e;
        } finally {
            restTemplate.setInterceptors(List.of());
        }
    }

    public SubmitDepartureDeclarationResponse createDepartureMovement(String xml, Optional<String> accessToken) throws RequestException, UnauthorizedException {
            logger.trace("Sending payload >>" + xml + "<<");

            final var headers = contentTypeHeaders(MediaType.APPLICATION_XML);
            HttpEntity<String> entity = new HttpEntity<>(xml, headers);
            try {
                return request(HttpMethod.POST, submitDepartureDeclarationUrl, null, entity, SubmitDepartureDeclarationResponse.class, accessToken).getBody();
            }
            catch (HttpClientErrorException | HttpServerErrorException e) {
                log.warn(e);
                throw new RequestException(e.getStatusCode().value(), e.getStatusCode().getReasonPhrase(), e.getResponseBodyAsString());
            }
    }

    public GetDepartureMessageIdsResponse getDepartureMessageIds(String departureId, Optional<Date> date,  Optional<String> accessToken) throws RequestException, NotFoundException, UnauthorizedException {
        logger.trace("Getting message IDs for departure ID {}", departureId);
        final var uriParameters = new ArrayList<Map.Entry<String, String>>();
        uriParameters.add(Map.entry("departureId", departureId));
        date.ifPresent(d -> uriParameters.add(Map.entry("receivedSince", DateTimeFormatter.ISO_DATE_TIME.format(d.toInstant()))));

        final var map = uriParameters.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        try {
            final var response = request(HttpMethod.GET, getDepartureMessageIDsUrl, map, null, GetDepartureMessageIdsResponse.class, accessToken);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            log.warn(e);
            throw new RequestException(e.getStatusCode().value(), e.getStatusCode().getReasonPhrase(), e.getResponseBodyAsString());
        }
    }

    public GetSingleDepartureMessageResponse getSingleDepartureMessage(String departureId, String messageId, Optional<String> accessToken) throws RequestException, NotFoundException, UnauthorizedException {
        logger.trace("Getting message ID {} for departure ID {}", messageId, departureId);
        final var uriParameters = Map.ofEntries(
                Map.entry("departureId", departureId),
                Map.entry("messageId", messageId)
        );
        try {
            final var response = request(HttpMethod.GET, getSingleDepartureMessageUrl, uriParameters, null, GetSingleDepartureMessageResponse.class, accessToken);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            log.warn(e);
            throw new RequestException(e.getStatusCode().value(), e.getStatusCode().getReasonPhrase(), e.getResponseBodyAsString());
        }
    }
    public GetDepartureResponse getDeparture(String departureId, Optional<String> accessToken) throws RequestException, NotFoundException, UnauthorizedException {
        logger.trace("Getting departure ID {}", departureId);
        final var uriParameters = Map.ofEntries(
            Map.entry("departureId", departureId)
        );
        try {
            final var response = request(HttpMethod.GET, getDepartureUrl, uriParameters, null, GetDepartureResponse.class, accessToken);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
            log.warn(e);
            throw new RequestException(e.getStatusCode().value(), e.getStatusCode().getReasonPhrase(), e.getResponseBodyAsString());
        }
    }

    private HttpHeaders contentTypeHeaders(MediaType mediaType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return headers;
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
            updateHeaders(wrapper.getHeaders());
            return execution.execute(wrapper, body);
        }

        private void updateHeaders(HttpHeaders headers) {
            headers.setAccept(List.of(MediaType.valueOf(acceptHeader)));
            accessToken.ifPresent(token -> headers.set("Authorization", "Bearer " + token));
            logger.debug(String.valueOf(headers));
        }
    }
}