package com.acme.fussballverein.repository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;

/**
 * "HTTP Interface" für den REST-Client für Trainerdaten.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@HttpExchange("/rest")
public interface TrainerRestRepository {
    /**
     * Einen Trainerndatensatz vom Microservice "Trainer" mit "Basic Authentication" anfordern.
     *
     * @param id ID des angeforderten Trainern
     * @param version Version des angeforderten Datensatzes
     * @return Gefundener Trainer
     */
    @GetExchange("/{id}")
    @SuppressWarnings("unused")
    ResponseEntity<Trainer> getTrainerMitVersion(
        @PathVariable String id,
        @RequestHeader(IF_NONE_MATCH) String version
    );

    /**
     * Einen Trainerndatensatz vom Microservice "Trainer" mit "Basic Authentication" anfordern.
     *
     * @param id ID des angeforderten Trainern
     * @return Gefundener Trainer
     */
    @GetExchange("/{id}")
    ResponseEntity<Trainer> getTrainer(
        @PathVariable String id
    );
}
