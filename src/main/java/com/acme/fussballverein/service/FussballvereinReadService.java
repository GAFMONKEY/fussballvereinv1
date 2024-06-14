package com.acme.fussballverein.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.repository.FussballvereinRepository;
import com.acme.fussballverein.repository.PredicateBuilder;
import com.acme.fussballverein.repository.Trainer;
import com.acme.fussballverein.repository.TrainerRestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;

/**
 * ReadService für Fussballvereine.
 * <img src="..\..\..\..\..\asciidoc\FussballvereinReadService.svg" alt="Klassendiagramm">
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FussballvereinReadService {
    private final FussballvereinRepository repo;
    private final TrainerRestRepository trainerRepository;
    private final HttpGraphQlClient graphQlClient;
    private final PredicateBuilder predicateBuilder;

    /**
     * Gibt Fussballverein mit bestimmter id zurück.
     *
     * @param id id des Fussballvereins
     * @return Fussballverein zu id.
     */
    public Fussballverein findByID(final UUID id) {
        final var fussballvereinOpt = repo.findById(id);
        if (fussballvereinOpt.isEmpty()) {
            throw new NotFoundException();
        }

        final var fussballverein = fussballvereinOpt.orElseThrow(NotFoundException::new);
        final var name = findTrainerById(fussballverein.getTrainerId()).name();
        fussballverein.setTrainerName(name);
        final var email = findEmailById(fussballverein.getTrainerId());
        fussballverein.setTrainerEmail(email);
        return fussballverein;
    }

    /**
     * Alle Fussballvereine ermitteln.
     *
     * @return Alle Fussballvereine.
     */
    public Collection<Fussballverein> findAll() {
        final var fussballvereine = repo.findAll();
        fussballvereine.forEach(fussballverein -> {
            final var trainerId = fussballverein.getTrainerId();
            final var trainer = findTrainerById(trainerId);
            final var email = findEmailById(trainerId);
            fussballverein.setTrainerName(trainer.name());
            fussballverein.setTrainerEmail(email);
        });
        return fussballvereine;
    }

    /**
     * Sucht Fussballvereine mit angegebenen Parametern.
     *
     * @param suchkriterien Suchkriterium zur Suche der Vereine
     * @return Liste mit Fussballvereinen zu angegebenem Suchkriterium
     */
    @SuppressWarnings({"ReturnCount", "NestedIfDepth", "CyclomaticComplexity", "NPathComplexity"})
    public @NonNull Collection<Fussballverein> find(
        @NonNull final Map<String, List<String>> suchkriterien
    ) {
        if (suchkriterien.isEmpty()) {
            return findAll();
        }

        if (suchkriterien.size() == 1) {
            final var namen = suchkriterien.get("name");
            if (namen != null && namen.size() == 1) {
                final var vereine = repo.findByName(namen.get(0));
                if (vereine.isEmpty()) {
                    throw new NotFoundException(suchkriterien);
                }
                return vereine;
            }

            final var plz = suchkriterien.get("plz");
            if (plz != null && plz.size() == 1) {
                final var vereine = repo.findByPlz(plz.get(0));
                if (vereine.isEmpty()) {
                    throw new NotFoundException(suchkriterien);
                }
                return vereine;
            }

            final var emails = suchkriterien.get("email");
            if (emails != null && emails.size() == 1) {
                final var fussballverein = repo.findByEmail(emails.get(0));
                if (fussballverein.isEmpty()) {
                    throw new NotFoundException(suchkriterien);
                }
                return List.of(fussballverein.get());
            }
        }

        final var predicate = predicateBuilder
            .build(suchkriterien)
            .orElseThrow(() -> new NotFoundException(suchkriterien));
        final var vereine = repo.findAll(predicate);
        if (vereine.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }
        return vereine;
    }

    /**
     * Fussballvereine zur Trainer-ID suchen.
     *
     * @param trainerId Die Id des gegebenen Trainers.
     * @return Die gefundenen Fussballvereine.
     * @throws NotFoundException Falls keine Fussballvereine gefunden wurden.
     */
    public Collection<Fussballverein> findByTrainerId(final UUID trainerId) {
        log.debug("findByTrainerId: trainerId={}", trainerId);

        final var fussballvereine = repo.findByTrainerId(trainerId);
        if (fussballvereine.isEmpty()) {
            throw new NotFoundException();
        }

        final var trainer = findTrainerById(trainerId);
        final var name = trainer == null ? null : trainer.name();
        final var email = findEmailById(trainerId);
        log.trace("findByTrainerId: name={}, email={}", name, email);
        fussballvereine.forEach(fussballverein -> {
            fussballverein.setTrainerName(name);
            fussballverein.setTrainerEmail(email);
        });

        log.trace("findByTrainerId: fussballvereine={}", fussballvereine);
        return fussballvereine;
    }

    @SuppressWarnings("ReturnCount")
    private Trainer findTrainerById(final UUID trainerId) {

        final ResponseEntity<Trainer> response;
        try {
            response = trainerRepository.getTrainer(trainerId.toString());
        } catch (final WebClientResponseException.NotFound ex) {
            // Statuscode 404
            log.error("findTrainerById: WebClientResponseException.NotFound");
            return new Trainer("N/A", "not.found@acme.com");
        } catch (final WebClientException ex) {
            // sonstiger Statuscode 4xx oder 5xx
            // WebClientRequestException oder WebClientResponseException (z.B. ServiceUnavailable)
            log.error("findTrainerById: {}", ex.getClass().getSimpleName());
            return new Trainer("Exception", "exception@acme.com");
        }

        final var statusCode = response.getStatusCode();
        log.debug("findTrainerById: statusCode={}", statusCode);
        if (statusCode == NOT_MODIFIED) {
            return new Trainer("Not-Modified", "not.modified@acme.com");
        }

        final var trainer = response.getBody();
        log.debug("findTrainerById: {}", trainer);
        return trainer;
    }

    /**
     * Abfrage, welche Namen es zu einem Präfix gibt.
     *
     * @param prefix Name-Präfix.
     * @return Die passenden Name.
     * @throws NotFoundException Falls keine Namen gefunden wurden.
     */
    public @NonNull Collection<String> findNamenByPrefix(final String prefix) {
        final var namen = repo.findNamenByPrefix(prefix);
        if (namen.isEmpty()) {
            throw new NotFoundException();
        }
        return namen;
    }

    private String findEmailById(final UUID trainerId) {
        final var query = """
            query {
                trainer(id: "%s") {
                    email
                }
            }
            """.formatted(trainerId);

        final String email;
        try {
            email = graphQlClient
                .mutate()
                .header(AUTHORIZATION)
                .build()
                .document(query)
                .retrieve("trainer")
                .toEntity(TrainerEmail.class)
                .map(TrainerEmail::email)
                .block();
        } catch (final FieldAccessException | GraphQlTransportException ex) {
            return "N/A";
        }
        return email;
    }
}
