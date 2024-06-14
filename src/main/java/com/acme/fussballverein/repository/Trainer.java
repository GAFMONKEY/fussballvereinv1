package com.acme.fussballverein.repository;

/**
 * Entity-Klasse für den REST-Client.
 *
 * @param name Nachname
 * @param email Emailadresse
 */
public record Trainer(String name, String email) {
}
