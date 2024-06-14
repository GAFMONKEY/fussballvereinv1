package com.acme.fussballverein.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * WebFilter zur Protokollierung des Request-Headers.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
interface LogRequestHeaders {
    /**
     * WebFilter zur Protokollierung des Request-Headers.
     *
     * @return CommonsRequestLoggingFilter, der den Request-Header protokolliert.
     */
    @Bean
    default CommonsRequestLoggingFilter logFilter() {
        // https://www.baeldung.com/spring-http-logging
        // https://stackoverflow.com/questions/33744875...
        // .../spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl
        // https://github.com/zalando/logbook
        final var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        return filter;
    }
}
