package com.acme.fussballverein.graphql;

import com.acme.fussballverein.service.NotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import static org.springframework.graphql.execution.ErrorType.NOT_FOUND;

/**
 * Abbildung von Exceptions auf GraphQLError.
 */
@ControllerAdvice
final class ExceptionHandler {
    @GraphQlExceptionHandler
    GraphQLError handleNotFound(final NotFoundException ex) {
        final var id = ex.getId();
        final var message = id == null
            ? "Kein Kunde gefunden: suchkriterien=" + ex.getSuchkriterien()
            : "Kein Kunde mit der ID " + id + " gefunden";
        return GraphQLError.newError()
            .errorType(NOT_FOUND)
            .message(message)
            .build();
    }
}
