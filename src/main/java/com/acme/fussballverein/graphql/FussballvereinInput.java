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
package com.acme.fussballverein.graphql;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.entity.Mannschaft;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Eine Value-Klasse für Eingabedaten passend zu KundeInput aus dem GraphQL-Schema.
 *
 * @param name Name des Vereins
 * @param email E-Mail des Vereins
 * @param gruendungsdatum Gruendungsdatum des Vereins
 * @param plz Postleitzahl des Vereins
 * @param telefonnummer Telefonnummer des Vereins
 * @param mannschaften Mannschaften des Vereins
 * @param trainerID ID des Trainers des Vereins
 */
@SuppressWarnings("RecordComponentNumber")
record FussballvereinInput(
    String name,
    String email,
    String gruendungsdatum,
    String plz,
    String telefonnummer,
    UUID trainerID,
    List<MannschaftInput> mannschaften
) {
    /**
     * Konvertierung in ein Objekt der Entity-Klasse Fussballverein.
     *
     * @return Das konvertierte Fussballverein-Objekt
     */
    Fussballverein toFussballverein() {
        final var gruendungsdatumTmp = LocalDate.parse(gruendungsdatum);
        final List<Mannschaft> mannschaftenEntity = mannschaften == null
            ? new ArrayList<>(1)
            : mannschaften.stream()
            .map(mannschaft -> Mannschaft.builder().jugend(mannschaft.jugend())
                .anzahlMitglieder(mannschaft.anzahlMitglieder()).build())
            .collect(Collectors.toList());


        final var fussballverein = Fussballverein
            .builder()
            .id(null)
            .name(name)
            .email(email)
            .gruendungsdatum(gruendungsdatumTmp)
            .telefonnummer(telefonnummer)
            .plz(plz)
            .trainerId(trainerID)
            .mannschaften(mannschaftenEntity)
            .build();

        mannschaftenEntity
            .forEach(mannschaft -> mannschaft.setFussballverein(fussballverein));

        return fussballverein;
    }
}
