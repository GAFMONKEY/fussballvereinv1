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
/**
 * Geldbetrag und Währungseinheit für eine Umsatzangabe.
 *
 * @param jugend Die Jugendart als unveränderliches Pflichtfeld.
 * @param anzahlMitglieder Die Anzahl der Mitglieder als unveränderliches Pflichtfeld.
 */
record MannschaftInput(String jugend, int anzahlMitglieder) {
}
