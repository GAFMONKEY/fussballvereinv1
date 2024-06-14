package com.acme.fussballverein.graphql;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.service.FussballvereinReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import static java.util.Collections.emptyMap;

/**
 * Eine Controller-Klasse für das Lesen mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
class FussballvereinQueryController {
    private final FussballvereinReadService service;

    /**
     * Suche anhand der Fussballverein-ID.
     *
     * @param id ID des zu suchenden Fussballvereins
     * @return Der gefundene Fussballverein
     */
    @QueryMapping
    Fussballverein fussballverein(@Argument final UUID id) {
        log.debug("fussballverein: id={}", id);
        final var fussballverein = service.findByID(id);
        log.debug("fussballverein: {}", fussballverein);
        return fussballverein;
    }

    /**
     * Suche mit diversen Suchkriterien.
     *
     * @param input Suchkriterien und ihre Werte, z.B. `name` und `VFR Rheinsheim`
     * @return Die gefundenen Fussballvereine als Collection
     */
    @QueryMapping
    Collection<Fussballverein> fussballvereine(@Argument final Optional<Suchkriterien> input) {
        log.debug("fussballvereine: suchkriterien={}", input);
        final var suchkriterien = input.map(Suchkriterien::toMap).orElse(emptyMap());
        final var fussballvereine = service.find(suchkriterien);
        log.debug("fussballvereine: {}", fussballvereine);
        return fussballvereine;
    }
}
