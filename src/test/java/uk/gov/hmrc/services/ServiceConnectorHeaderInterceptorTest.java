package uk.gov.hmrc.services;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class ServiceConnectorHeaderInterceptorTest {

    @SuppressWarnings("resource") // our intercept method doesn't return anything that needs to be closed.
    @Test
    public void interceptorShouldAddAcceptAndAuthorizationHeaders() throws Exception {
        Optional<String> accessToken = Optional.of("abcdefghijklmno");
        var sut = new ServiceConnector.HeaderRequestInterceptor(accessToken, "application/json");
        var result = sut.intercept(new TestHttpRequest(), new byte[0], (req, body) -> new TestHttpResponse(req.getHeaders()));
        var headers = result.getHeaders();

        assertThat(headers.get("Authorization"), hasItem("Bearer " + accessToken.get()));
        assertThat(headers.getAccept(), hasItem(MediaType.valueOf("application/json")));
    }

    private static class TestHttpRequest implements HttpRequest {

        private final HttpHeaders headers = new HttpHeaders();

        @Override
        public String getMethodValue() {
            return "GET";
        }

        @Override
        public URI getURI() {
            return URI.create("http://where.the/skies/are/blue");
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }
    }

    private static class TestHttpResponse implements ClientHttpResponse {

        private final HttpHeaders headers;

        TestHttpResponse(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.ACCEPTED;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return 202;
        }

        @Override
        public String getStatusText() throws IOException {
            return "ACCEPTED";
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getBody() throws IOException {
            return null;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }
    }

}
