package com.acme.fussballverein.rest;

import com.acme.fussballverein.entity.Fussballverein;
import com.acme.fussballverein.entity.Mannschaft;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Transfer Object der Klasse Fussballverein.
 *
 * @param name Name des Fussballvereins.
 * @param email E-Mail des Fussballvereins.
 * @param gruendungsdatum Gruendungsdatum des Fussballvereins.
 * @param plz Postleitzahl des Fussballvereins.
 * @param telefonnummer Telefonnummer des Fussballvereins.
 * @param trainerID ID des Trainers des Vereins
 * @param mannschaften Mannschaften des Fussballvereins.
 */
record FussballvereinDTO(
    String name,
    String email,
    LocalDate gruendungsdatum,
    String plz,
    String telefonnummer,
    UUID trainerID,
    List<MannschaftDTO> mannschaften
) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns.
     *
     * @return Fussballverein-Objekt für den Anwendungskern
     */
    Fussballverein toFussballverein() {
        final List<Mannschaft> mannschaftenEntity = mannschaften == null
            ? new ArrayList<>(1)
            : mannschaften.stream()
            .map(mannschaft -> Mannschaft.builder()
                .jugend(mannschaft.jugend())
                .anzahlMitglieder(mannschaft.anzahlMitglieder())
                .build()
            ).collect(Collectors.toList());

        final var fussballverein = Fussballverein
            .builder()
            .id(null)
            .email(email)
            .gruendungsdatum(gruendungsdatum)
            .mannschaften(mannschaftenEntity)
            .telefonnummer(telefonnummer)
            .plz(plz)
            .trainerId(trainerID)
            .name(name)
            .build();

        //Rueckwaertsverweise
        mannschaftenEntity
            .forEach(mannschaft -> mannschaft.setFussballverein(fussballverein));

        return fussballverein;
    }
}
