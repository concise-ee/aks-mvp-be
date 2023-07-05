package ee.kemit.aks.config;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class LoggingConfiguration {

    public static String REQUEST_URL = "X-Request-URL";
    public static String REQUEST_ID = "X-Request-ID";
    public static String TRACE_ID = "X-Trace-ID";

    @Bean
    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {

        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean oncePerRequestFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(this.getOncePerRequestFilter());
        return registration;
    }

    private OncePerRequestFilter getOncePerRequestFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

                String requestId = LoggingConfiguration.this.getRequestId(httpServletRequest);
                String traceId = LoggingConfiguration.this.getTraceId(httpServletRequest);

                long startTime = System.currentTimeMillis();

                String queryString = (httpServletRequest.getQueryString() == null) ? "" : ("qs:" + httpServletRequest.getQueryString());
                try {

                    String requestUrl = httpServletRequest.getRequestURI();
                    ThreadContext.put(REQUEST_ID, requestId);
                    ThreadContext.put(REQUEST_URL, requestUrl);
                    ThreadContext.put(TRACE_ID, traceId);

                    log.info("{} {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), queryString);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } finally {
                    String duration = String.valueOf(System.currentTimeMillis() - startTime);
                    log.info("{} {} {}, handling took: {} ms", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), queryString, duration);
                    ThreadContext.clearAll();
                }
            }
        };
    }

    private String getRequestId(HttpServletRequest request) {

        String requestId = request.getHeader(REQUEST_ID);
        if (requestId == null || requestId.length() < 1) {
            requestId = UUID.randomUUID().toString();
        }

        return requestId;
    }

    private String getTraceId(HttpServletRequest request) {

        String traceId = request.getHeader(TRACE_ID);
        if (traceId == null || traceId.length() < 1) {
            return null;
        }
        return traceId;
    }
}

