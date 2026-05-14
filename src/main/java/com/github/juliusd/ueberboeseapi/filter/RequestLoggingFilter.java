package com.github.juliusd.ueberboeseapi.filter;

import static org.slf4j.LoggerFactory.getLogger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger EVENT_LOG = getLogger("com.github.juliusd.ueberboeseapi.EventLog");

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String fullUri = queryString != null ? uri + "?" + queryString : uri;
    log.info("Request: {} {}", request.getMethod(), fullUri);

    boolean isEventReport = uri.matches(".*/v1/scmudc/.*");
    boolean isBmxReport = uri.matches(".*/bmx/.+/v1/report.*");
    boolean isPost = "POST".equalsIgnoreCase(request.getMethod());

    if (isEventReport || isBmxReport || isPost) {
      ContentCachingRequestWrapper wrappedRequest =
          new ContentCachingRequestWrapper(request, 1024 * 1024);

      // Continue with the filter chain
      filterChain.doFilter(wrappedRequest, response);

      // Log the raw request body after the request has been processed
      byte[] content = wrappedRequest.getContentAsByteArray();
      if (content.length > 0) {
        String rawBody = new String(content, StandardCharsets.UTF_8);
        if (isEventReport) {
          EVENT_LOG.info("event: {}", rawBody.trim());
        } else if (isBmxReport) {
          EVENT_LOG.info("bxm-report: {}", rawBody.trim());
        } else if (response.getStatus() >= 400) {
          log.warn("POST {} failed (HTTP {}): {}", uri, response.getStatus(), rawBody.trim());
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
