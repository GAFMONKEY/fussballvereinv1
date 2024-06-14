package com.acme.fussballverein.rest;

/**
 * Data Transfer Object der Klasse Mannschaft.
 *
 * @param jugend Die Jugendklasse der Mannschaft.
 * @param anzahlMitglieder Die Anzahl der Mitglieder.
 */
record MannschaftDTO(
    String jugend,
    int anzahlMitglieder
) {
}
