package com.acme.fussballverein.rest;

import com.acme.fussballverein.service.ConstraintViolationsException;
import com.acme.fussballverein.service.EmailExistsException;
import com.acme.fussballverein.service.FussballvereinWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import static com.acme.fussballverein.rest.FussballvereinGetController.REST_PATH;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static com.acme.fussballverein.rest.FussballvereinGetController.ID_PATTERN;
import static org.springframework.http.ResponseEntity.noContent;

/**
 * Klasse zum Schreiben der Fussballverein Daten.
 */
@RequiredArgsConstructor
@RequestMapping(REST_PATH)
@Slf4j
@Controller
@SuppressWarnings("ClassFanOutComplexity")
public class FussballvereinWriteController {
    /**
     * Pfad für Problem bei HTTP-Request.
     */
    static final String PROBLEM_PATH = "/problem/";
    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";
    private final FussballvereinWriteService service;
    private final UriHelper uriHelper;


    /**
     * Methode zum Anlegen eines neuen Fussballvereins.
     *
     * @param fussballvereinDTO Data-Transfer-Objekt eines Fussballvereins
     * @param request Request
     * @return UUID des neuen Fussballvereins
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Fussballverein anlegen.", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Fussballverein neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> create(@RequestBody final FussballvereinDTO fussballvereinDTO,
                                final HttpServletRequest request) {
        log.debug("create: {}", fussballvereinDTO);
        final var fussballverein = service.create(fussballvereinDTO.toFussballverein());
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var location = URI.create(baseUri + '/' + fussballverein.getId());
        return created(location).build();
    }

    /**
     * Einen vorhandenen Fussballverein-Datensatz überschreiben.
     *
     * @param id ID des zu aktualisierenden Fussballverein.
     * @param fussballvereinDTO Das Fussballverein-Objekt aus dem eingegangenen Request-Body.
     * @param version Version des Fussballvereins.
     * @param request Http-Request des Fussballvereins.
     * @return ResponseEntity vom Typ Fussballverein.
     */
    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Fussballverein mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Fussballverein nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> update(@PathVariable final UUID id, @RequestBody final FussballvereinDTO fussballvereinDTO,
                @RequestHeader("If-Match") final Optional<String> version,
                final HttpServletRequest request) {
        final int versionInt = getVersion(version, request);
        final var fussballverein = service.update(fussballvereinDTO.toFussballverein(), id, versionInt);
        return noContent().eTag("\"" + fussballverein.getVersion() + '"').build();
    }

    /**
     * Einen vorhandenen Kunden anhand seiner ID löschen.
     *
     * @param id ID des zu löschenden Kunden.
     */
    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Fussballverein anhand der ID loeschen", tags = "Loeschen")
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    void deleteById(@PathVariable final UUID id)  {
        service.deleteById(id);
    }

    /**
     * ExceptionHandler, wenn fehlerhafte Daten bei Put-Request übertragen werden.
     *
     * @param ex Exception
     * @param request HTTP Request
     * @return ProblemDetail
     */
    @ExceptionHandler
    ProblemDetail onConstraintViolations(
        final ConstraintViolationsException ex,
        final HttpServletRequest request
    ) {
        log.debug("onConstraintViolations: {}", ex.getMessage());
        final var vereinViolations = ex.getViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " +
                violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " " +
                violation.getMessage())
            .toList();
        log.trace("onConstraintViolations: {}", vereinViolations);
        final String detail;
        if (vereinViolations.isEmpty()) {
            detail = "N/A";
        } else {
            final var violationsStr = vereinViolations.toString();
            detail = violationsStr.substring(1, violationsStr.length() - 2);
        }
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(PROBLEM_PATH));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onEmailExists(
        final EmailExistsException ex,
        final HttpServletRequest request
    ) {
        final var problemEmail = ex.getEmail();
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY,
            "Fussballverein mit der Email '" + problemEmail + "' existiert bereits.");
        problemDetail.setType(URI.create(PROBLEM_PATH));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
        if (versionOpt.isEmpty()) {
            throw new VersionInvalidException(
                PRECONDITION_REQUIRED,
                VERSIONSNUMMER_FEHLT,
                URI.create(request.getRequestURL().toString()));
        }
        final var versionStr = versionOpt.get();
        if (versionStr.length() < 3 ||
            versionStr.charAt(0) != '"' ||
            versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                "Ungueltiges ETag " + versionStr,
                URI.create(request.getRequestURL().toString())
            );
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                "Ungueltiges ETag " + versionStr,
                URI.create(request.getRequestURL().toString()),
                ex
            );
        }

        return version;
    }
}
