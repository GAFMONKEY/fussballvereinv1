/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.fussballverein.service;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.repository.FussballvereinRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Anwendungslogik für Fussballvereine auch mit Bean Validation.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FussballvereinWriteService {
    private final FussballvereinRepository repo;
    // https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation-beanvalidation
    private final Validator validator;


    /**
     * Einen neuen Fussballvereine anlegen.
     *
     * @param fussballverein Das Objekt des neu anzulegenden Fussballvereine.
     * @return Der neu angelegte Fussballvereine mit generierter ID
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws EmailExistsException Es gibt bereits einen Fussballvereine mit der Emailadresse.
     */
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#transactions
    @Transactional
    @SuppressWarnings("TrailingComment")
    public Fussballverein create(final Fussballverein fussballverein) {
        final var violations = validator.validate(fussballverein);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationsException(violations);
        }

        if (repo.existsByEmail(fussballverein.getEmail())) {
            throw new EmailExistsException(fussballverein.getEmail());
        }

        return repo.save(fussballverein);
    }

    /**
     * Einen vorhandenen Fussballvereine aktualisieren.
     *
     * @param fussballverein Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Fussballvereine
     * @param version Die erforderliche Version
     * @return Aktualisierter Fussballverein mit erhöhter Versionsnummer
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws NotFoundException Kein Fussballverein zur ID vorhanden.
     * @throws VersionOutdatedException Die Versionsnummer ist veraltet und nicht aktuell.
     * @throws EmailExistsException Es gibt bereits einen Fussballverein mit der Emailadresse.
     */
    @Transactional
    public Fussballverein update(final Fussballverein fussballverein, final UUID id, final int version) {
        final var violations = validator.validate(fussballverein);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationsException(violations);
        }

        final var vereinDbOptional = repo.findById(id);
        if (vereinDbOptional.isEmpty()) {
            throw new NotFoundException(id);
        }

        var fussballvereinDb = vereinDbOptional.get();
        if (version != fussballvereinDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        final var email = fussballverein.getEmail();
        // Ist die neue E-Mail bei einem *ANDEREN* Fussballvereine vorhanden?
        if (!Objects.equals(email, fussballvereinDb.getEmail()) && repo.existsByEmail(email)) {
            log.debug("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }

        fussballvereinDb.set(fussballverein);
        fussballvereinDb = repo.save(fussballvereinDb);
        return fussballvereinDb;
    }

    /**
     * Einen Fussballvereine löschen.
     *
     * @param id Die ID des zu löschenden Fussballvereine.
     */
    @Transactional
    public void deleteById(final UUID id) {
        final var vereinOptional = repo.findById(id);
        if (vereinOptional.isEmpty()) {
            return;
        }
        repo.delete(vereinOptional.get());
    }
}
